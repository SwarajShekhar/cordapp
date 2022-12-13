package com.modeln.flows.memberState.initiators;

import co.paralleluniverse.fibers.Suspendable;
import com.modeln.contracts.memberstate.MemberStateContract;
import com.modeln.exceptions.RecordAlreadyExistsException;
import com.modeln.states.memberstate.MemberState;
import com.modeln.utils.FlowUtility;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

public class ModelNAddMemberState {

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<UniqueIdentifier>{

        private String memberName;
        private String memberType;
        private String description;
        private String DEAID;
        private String DDDID;
        private String status;
        private String addres;

        public Initiator(String memberName, String memberType, String description, String DEAID, String DDDID, String status, String addres) {
            this.memberName = memberName;
            this.memberType = memberType;
            this.description = description;
            this.DEAID = DEAID;
            this.DDDID = DDDID;
            this.status = status;
            this.addres = addres;
        }

        @Override
        @Suspendable
        public UniqueIdentifier call() throws FlowException {
            Party me = getOurIdentity();

            //Compose the Member State
            final MemberState output = new MemberState(me,new UniqueIdentifier(), memberName, memberType, description,
                    DEAID, DDDID, status, addres, Instant.now());

            // Check if the record exists
            QueryCriteria.VaultQueryCriteria inputCriteria = new QueryCriteria.VaultQueryCriteria()
                    .withStatus(Vault.StateStatus.UNCONSUMED)
                    .withRelevancyStatus(Vault.RelevancyStatus.RELEVANT);
            List<StateAndRef<MemberState>> memberStateRefList = getServiceHub().getVaultService().queryBy(MemberState.class, inputCriteria).getStates();
            MemberState memberStateFromQuery = null;
            for(StateAndRef<MemberState> memberStateRef: memberStateRefList){
                memberStateFromQuery = (MemberState) memberStateRef.getState().getData();
                if(memberStateFromQuery.equals(output)){
                    throw new RecordAlreadyExistsException("Record Already Exists. " + output);
                }
            }
            final Party notary = FlowUtility.getNotary(getServiceHub());
            final TransactionBuilder builder = new TransactionBuilder(notary);

            builder.addOutputState(output);
            builder.addCommand(new MemberStateContract.Commands.Approve(), getOurIdentity().getOwningKey() );

            builder.verify(getServiceHub());

            final SignedTransaction ptx = getServiceHub().signInitialTransaction(builder);

            subFlow(new FinalityFlow(ptx, Collections.emptyList()));

            return output.getLinearId();

        }
    }
}