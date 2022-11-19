package com.modeln.flows.membershipState.initiator;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.identity.Party;

@InitiatingFlow
public class ValidateLinearIdRequest extends FlowLogic<Boolean> {
    private final Party oracle;
    private final String linearId;

    public ValidateLinearIdRequest(Party oracle, String linearId) {
        this.oracle = oracle;
        this.linearId = linearId;
    }

    @Suspendable
    @Override
    public Boolean call() throws FlowException {
        String response = initiateFlow(oracle).sendAndReceive(String.class, linearId).unwrap(it -> it);
        return Boolean.getBoolean(response);
    }
}
