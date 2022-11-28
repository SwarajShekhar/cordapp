package com.modeln.flows.memberState.initiators;

import co.paralleluniverse.fibers.Suspendable;
import com.modeln.contracts.memberstate.MemberStateProposalContract;
import com.modeln.enums.memberstateproposal.MemberStateProposalStatus;
import com.modeln.flows.memberState.ModelNBroadcastMemberState;
import com.modeln.states.memberstate.MemberStateProposal;
import com.modeln.utils.FlowUtility;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.util.Arrays;
import java.util.List;

@InitiatingFlow
@StartableByRPC
public class RespondToAddMemberRequestProposalRequest extends FlowLogic<UniqueIdentifier> {

    private final UniqueIdentifier memberStateProposalIdentifier;
    private final MemberStateProposalStatus status;

    public RespondToAddMemberRequestProposalRequest(UniqueIdentifier memberStateProposalIdentifier, MemberStateProposalStatus status) {
        this.memberStateProposalIdentifier = memberStateProposalIdentifier;
        this.status = status;
    }

    @Override
    @Suspendable
    public UniqueIdentifier call() throws FlowException {
        // Check if the record exists
        QueryCriteria.LinearStateQueryCriteria inputCriteria = new QueryCriteria.LinearStateQueryCriteria()
                .withStatus(Vault.StateStatus.UNCONSUMED)
                .withRelevancyStatus(Vault.RelevancyStatus.RELEVANT).withUuid(Arrays.asList(memberStateProposalIdentifier.getId()));
        List<StateAndRef<MemberStateProposal>> memberStateProposalStateRef =
                getServiceHub().getVaultService().queryBy(MemberStateProposal.class, inputCriteria).getStates();
        if(memberStateProposalStateRef.size() != 1)
            throw new IllegalArgumentException("Invalid UUID: " + memberStateProposalIdentifier.getId().toString() +
                    "\t Expected 1, but found: " + memberStateProposalStateRef.size());

        // input object
        MemberStateProposal memberStateProposalFromQuery =
                (MemberStateProposal) memberStateProposalStateRef.get(0).getState().getData();

        // create output object
        MemberStateProposal output = new MemberStateProposal(
                memberStateProposalIdentifier,
                getOurIdentity(),
                memberStateProposalFromQuery.getMemberName(),
                memberStateProposalFromQuery.getMemberType(),
                memberStateProposalFromQuery.getDescription(),
                memberStateProposalFromQuery.getDEAID(),
                memberStateProposalFromQuery.getDDDID(),
                memberStateProposalFromQuery.getMemberStatus(),
                memberStateProposalFromQuery.getAddress(),
                this.status,
                memberStateProposalFromQuery.getOwner()
        );
        UniqueIdentifier uuid = null;
        if(status == MemberStateProposalStatus.APPROVED){
            uuid = createMemberState(memberStateProposalFromQuery);
            broadCastMemberState(uuid);
        }

        final Party notary = FlowUtility.getNotary(getServiceHub());

        final TransactionBuilder builder = new TransactionBuilder(notary);

        builder.addOutputState(output).addInputState(memberStateProposalStateRef.get(0));
        builder.addCommand(new MemberStateProposalContract.Commands.Respond(),
                Arrays.asList(getOurIdentity().getOwningKey(), memberStateProposalFromQuery.getOwner().getOwningKey() ));
        builder.verify(getServiceHub());

        builder.verify(getServiceHub());
        final SignedTransaction ptx = getServiceHub().signInitialTransaction(builder);

        // initiate flow
        FlowSession session = initiateFlow(memberStateProposalFromQuery.getOwner());

        // collect signatures
        SignedTransaction stx = subFlow(new CollectSignaturesFlow(ptx, Arrays.asList(session)));
        subFlow(new FinalityFlow(stx, Arrays.asList(session)));

        return uuid;
    }

    @Suspendable
    private UniqueIdentifier createMemberState(MemberStateProposal memberStateProposalFromQuery) throws FlowException {
        UniqueIdentifier uuid =
                subFlow(new ModelNAddMemberState.Initiator(
                        memberStateProposalFromQuery.getMemberName(),
                        memberStateProposalFromQuery.getMemberType(),
                        memberStateProposalFromQuery.getDescription(),
                        memberStateProposalFromQuery.getDEAID(),
                        memberStateProposalFromQuery.getDDDID(),
                        memberStateProposalFromQuery.getMemberStatus(),
                        memberStateProposalFromQuery.getAddress()
                ));
        return uuid;
    }

    @Suspendable
    private void broadCastMemberState(UniqueIdentifier uuid) throws FlowException {
        //Get all peers for broadcasting
        List<AbstractParty> broadcastingMember = FlowUtility.getAllPartyForBroadcast(getServiceHub(), getOurIdentity());
        subFlow(new ModelNBroadcastMemberState.Initiator(uuid.getId(), broadcastingMember));
    }
}
