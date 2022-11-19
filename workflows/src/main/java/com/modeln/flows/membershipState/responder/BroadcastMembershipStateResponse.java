package com.modeln.flows.membershipState.responder;

import co.paralleluniverse.fibers.Suspendable;
import com.modeln.flows.membershipState.initiator.BroadcastMembershipStateRequest;
import net.corda.core.flows.*;
import net.corda.core.node.StatesToRecord;

@InitiatedBy(BroadcastMembershipStateRequest.class)
public class BroadcastMembershipStateResponse extends FlowLogic<Void> {

    private FlowSession counterpartySession;

    public BroadcastMembershipStateResponse(FlowSession counterpartySession) {
        this.counterpartySession = counterpartySession;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {
        subFlow(new ReceiveTransactionFlow(counterpartySession, true, StatesToRecord.ALL_VISIBLE));
        return null;
    }
}
