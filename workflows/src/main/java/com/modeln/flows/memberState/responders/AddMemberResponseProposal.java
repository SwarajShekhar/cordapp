package com.modeln.flows.memberState.responders;

import co.paralleluniverse.fibers.Suspendable;
import com.modeln.flows.memberState.initiators.AddMemberRequestProposal;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

@InitiatedBy(AddMemberRequestProposal.class)
public class AddMemberResponseProposal extends FlowLogic<Void> {

    private FlowSession counterpartySession;
    public AddMemberResponseProposal(FlowSession counterpartySession) {
        this.counterpartySession = counterpartySession;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {
        SignedTransaction signedTransaction = subFlow(new SignTransactionFlow(counterpartySession) {
            @Suspendable
            @Override
            protected void checkTransaction(SignedTransaction stx) throws FlowException {

            }
        });
        subFlow(new ReceiveFinalityFlow(counterpartySession, signedTransaction.getId()));
        return null;
    }
}
