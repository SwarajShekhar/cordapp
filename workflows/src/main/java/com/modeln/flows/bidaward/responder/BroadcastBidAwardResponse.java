package com.modeln.flows.bidaward.responder;

import co.paralleluniverse.fibers.Suspendable;
import com.modeln.flows.bidaward.initiator.BroadcastBidAwardRequest;
import net.corda.core.flows.*;
import net.corda.core.node.StatesToRecord;

@InitiatedBy(BroadcastBidAwardRequest.class)
public class BroadcastBidAwardResponse extends FlowLogic<Void> {
    //private variable
    private FlowSession counterpartySession;

    //Constructor
    public BroadcastBidAwardResponse(FlowSession counterpartySession) {
        this.counterpartySession = counterpartySession;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {
        // Receive the transaction and store all its states.
        // If we don't pass `ALL_VISIBLE`, only the states for which the node is one of the `participants` will be stored.
        subFlow(new ReceiveTransactionFlow(counterpartySession, true, StatesToRecord.ALL_VISIBLE));

        return null;
    }
}
