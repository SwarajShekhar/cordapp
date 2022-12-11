package com.modeln.flows.memberState.responders;

import co.paralleluniverse.fibers.Suspendable;
import com.modeln.enums.memberstateproposal.MemberStateProposalStatus;
import com.modeln.flows.memberState.initiators.RespondToAddMemberRequestProposalRequest;
import com.modeln.flows.membershipState.initiator.AddMemberShipStateRequest;
import com.modeln.flows.membershipState.initiator.BroadcastMembershipStateRequest;
import com.modeln.states.memberstate.MemberStateProposal;
import com.modeln.utils.FlowUtility;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.transactions.SignedTransaction;

import java.time.Instant;
import java.util.List;

@InitiatedBy(RespondToAddMemberRequestProposalRequest.class)
public class RespondToAddMemberRequestProposalResponse extends FlowLogic<Void> {

    private FlowSession counterpartySession;

    public RespondToAddMemberRequestProposalResponse(FlowSession counterpartySession) {
        this.counterpartySession = counterpartySession;
    }

    @Override
    @Suspendable
    public Void call() throws FlowException {
        SignedTransaction signedTransaction = subFlow(new SignTransactionFlow(counterpartySession) {
            @Suspendable
            @Override
            protected void checkTransaction(SignedTransaction stx) throws FlowException {
                ContractState contractState = stx.getTx().getOutputs().get(0).getData();
                MemberStateProposal memberStateProposal = (MemberStateProposal)contractState;
                if(memberStateProposal.getMemberStateProposalStatus() == MemberStateProposalStatus.APPROVED ||
                memberStateProposal.getMemberStateProposalStatus() == MemberStateProposalStatus.CHANGED_AND_APPROVED) {
                    addAndBroadcastMemberShip(memberStateProposal.getMemberIdIdentifier().getPointer().getId().toString(),
                            memberStateProposal.getStartDate(), memberStateProposal.getEndDate());
                }
            }
        });
        subFlow(new ReceiveFinalityFlow(counterpartySession, signedTransaction.getId()));

        return null;
    }

    @Suspendable
    private UniqueIdentifier addAndBroadcastMemberShip(String memberStateUUID, Instant startDate, Instant endDate) throws FlowException {
        // Create MemberShip
        UniqueIdentifier uuid =
                subFlow(new AddMemberShipStateRequest(
                        memberStateUUID,
                        startDate,
                        endDate
                ));
        //Get all peers for broadcasting
        List<AbstractParty> broadcastingMember = FlowUtility.getAllPartyForBroadcast(getServiceHub(), getOurIdentity());
        subFlow(new BroadcastMembershipStateRequest(uuid.getId().toString(), broadcastingMember));
        return uuid;
    }
}
