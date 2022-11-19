package com.modeln.flows.membershipState.responder;

import co.paralleluniverse.fibers.Suspendable;
import com.modeln.flows.membershipState.initiator.ValidateLinearIdRequest;
import com.modeln.services.Oracle;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatedBy;

@InitiatedBy(ValidateLinearIdRequest.class)
public class ValidateLinearIdResponse extends FlowLogic<Void> {

    private final FlowSession session;

    public ValidateLinearIdResponse(FlowSession session) {
        this.session = session;
    }

    @Override
    @Suspendable
    public Void call() throws FlowException {
        Boolean response;
        String request = session.receive(String.class).unwrap(it -> it);
        try {
            // Get the nth prime from the oracle.
            response = getServiceHub().cordaService(Oracle.class).isLinerIdvalid(request);
        } catch (Exception e) {
            // Re-throw the exception as a FlowException so its propagated to the querying node.
            throw new FlowException(e);
        }

        session.send(response.toString());
        return null;
    }
}
