package com.modeln.flows.membershipState.responder;

import co.paralleluniverse.fibers.Suspendable;
import com.modeln.flows.membershipState.initiator.AddMemberShipStateRequest;
import net.corda.core.contracts.ContractState;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

@InitiatedBy(AddMemberShipStateRequest.class)
public class AddMemberShipStateResponse extends FlowLogic<Void> {

    private FlowSession counterpartySession;

    public AddMemberShipStateResponse(FlowSession counterpartySession) {
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
                // check if linearState is present
            }
        });
        subFlow(new ReceiveFinalityFlow(counterpartySession, signedTransaction.getId()));
        return null;
    }
}
