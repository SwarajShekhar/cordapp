package com.modeln.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

@InitiatedBy(ModelNAddMemberStateRequestFrom3rdParty.Initiator.class)
public class OrcaleAddFlow extends FlowLogic<Void> {

    private FlowSession session;

    public OrcaleAddFlow(FlowSession session) {
        this.session = session;
    }

    @Override
    public Void call() throws FlowException {
        SignedTransaction signedTransaction = subFlow(new SignTransactionFlow(session) {
                @Suspendable
                @Override
                protected void checkTransaction(SignedTransaction stx) throws FlowException {
                   session.send("11111");
                }
        });
        return null;
    }
}
