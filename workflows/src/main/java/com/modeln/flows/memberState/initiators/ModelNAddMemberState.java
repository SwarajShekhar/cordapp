package com.modeln.flows.memberState.initiators;

import co.paralleluniverse.fibers.Suspendable;
import com.modeln.contracts.memberstate.MemberStateContract;
import com.modeln.exceptions.RecordAlreadyExistsException;
import com.modeln.states.memberstate.MemberState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.util.Collections;
import java.util.List;

public class ModelNAddMemberState {

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<UniqueIdentifier>{

        private Party proposer;
        private Party sender;
        private String memberName;
        private String memberType;
        private String description;
        private String DEAID;
        private String DDDID;
        private String status;

        public Initiator(String memberName, String memberType) {
            this.memberName = memberName;
            this.memberType = memberType;
        }

        @Override
        @Suspendable
        public UniqueIdentifier call() throws FlowException {
            //Hello World message
            System.out.println(this.getClass().getSimpleName() + " call --> Entering...");
            this.proposer = getOurIdentity();
            System.out.println(this.getClass().getSimpleName() + " call --> Proposer: " + proposer.getName());
            this.sender = getServiceHub().getNetworkMapCache().getPeerByLegalName(CordaX500Name.parse("O=ModelN,L=London,C=GB"));

            //Compose the Member State
            final MemberState output = new MemberState(sender,new UniqueIdentifier(), memberName, memberType, description, DEAID, DDDID, status);

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
            System.out.println(this.getClass().getSimpleName() + " call --> Query executed...");
            final Party notary = getServiceHub().getNetworkMapCache().getNotary(CordaX500Name.parse("O=Notary,L=London,C=GB"));
            System.out.println(this.getClass().getSimpleName() + " call --> Notary set...");
            final TransactionBuilder builder = new TransactionBuilder(notary);
            System.out.println(this.getClass().getSimpleName() + " call --> Transaction builder...");

            builder.addOutputState(output);
            builder.addCommand(new MemberStateContract.Commands.Send(), getOurIdentity().getOwningKey() );
            System.out.println(this.getClass().getSimpleName() + " call --> Command Added...");

            builder.verify(getServiceHub());
            System.out.println(this.getClass().getSimpleName() + " call --> Verification done...");

            final SignedTransaction ptx = getServiceHub().signInitialTransaction(builder);
            System.out.println(this.getClass().getSimpleName() + " call --> Initial Signed Transaction...");

            subFlow(new FinalityFlow(ptx, Collections.emptyList()));

            System.out.println(this.getClass().getSimpleName() + " call --> After finality");

            return output.getLinearId();

        }
    }
}