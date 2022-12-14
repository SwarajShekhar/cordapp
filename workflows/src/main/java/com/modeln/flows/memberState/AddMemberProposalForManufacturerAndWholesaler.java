package com.modeln.flows.memberState;

import co.paralleluniverse.fibers.Suspendable;
import com.modeln.contracts.memberstate.MemberStateProposalContract;
import com.modeln.enums.memberstateproposal.MemberStateProposalStatus;
import com.modeln.states.memberstate.MemberState;
import com.modeln.states.memberstate.MemberStateProposal;
import com.modeln.utils.FlowUtility;
import net.corda.core.contracts.LinearPointer;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class AddMemberProposalForManufacturerAndWholesaler {

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<UniqueIdentifier> {

        private UniqueIdentifier linearId;
        private UUID memberSatetUUID;
        private LinearPointer<MemberState> memberStateLinearPointer;
        private String internalName;
        private String additionalInfo;

        public Initiator(UUID memberSatetUUID, String internalName, String additionalInfo) {
            this.memberSatetUUID = memberSatetUUID;
            this.internalName = internalName;
            this.additionalInfo = additionalInfo;
        }

        @Override
        @Suspendable
        public UniqueIdentifier call() throws FlowException {
            Party me = getOurIdentity();

            // Check if the record exists
            QueryCriteria.LinearStateQueryCriteria inputCriteria = new QueryCriteria.LinearStateQueryCriteria()
                    .withStatus(Vault.StateStatus.UNCONSUMED)
                    .withRelevancyStatus(Vault.RelevancyStatus.ALL)
                    .withUuid(Arrays.asList(memberSatetUUID));
            List<StateAndRef<MemberState>> memberStateRefList = getServiceHub().getVaultService().queryBy(MemberState.class, inputCriteria).getStates();
            MemberState memberStateFromQuery = null;
            memberStateFromQuery = (MemberState) memberStateRefList.get(0).getState().getData();

            linearId = new UniqueIdentifier();

            MemberStateProposal output = new MemberStateProposal(
                    linearId,
                    me,
                    memberStateFromQuery.getMemberName(),
                    memberStateFromQuery.getMemberType(),
                    memberStateFromQuery.getDescription(),
                    memberStateFromQuery.getDEAID(),
                    memberStateFromQuery.getDDDID(),
                    memberStateFromQuery.getStatus(),
                    memberStateFromQuery.getAddress(),
                    MemberStateProposalStatus.ADDED,
                    me,
                    new LinearPointer<>(new UniqueIdentifier(null, memberSatetUUID), MemberState.class),
                    null,
                    null,
                    this.internalName,
                    this.additionalInfo,
                    Instant.now()

            );

            final Party notary = FlowUtility.getNotary(getServiceHub());
            final TransactionBuilder builder = new TransactionBuilder(notary);

            builder.addOutputState(output);
            builder.addCommand(new MemberStateProposalContract.Commands.Send(), me.getOwningKey() );

            builder.verify(getServiceHub());

            final SignedTransaction ptx = getServiceHub().signInitialTransaction(builder);

            subFlow(new FinalityFlow(ptx, Collections.emptyList()));

            return output.getLinearId();

        }
    }

}
