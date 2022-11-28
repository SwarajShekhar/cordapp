package com.modeln.flows.memberState.responders;

import co.paralleluniverse.fibers.Suspendable;
import com.modeln.flows.memberState.initiators.RespondToAddMemberRequestProposalRequest;
import net.corda.core.contracts.ContractState;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

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
            }
        });
        subFlow(new ReceiveFinalityFlow(counterpartySession, signedTransaction.getId()));

        return null;
    }
}
