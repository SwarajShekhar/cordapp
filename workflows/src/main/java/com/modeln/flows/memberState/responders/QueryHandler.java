package com.modeln.flows.memberState.responders;

import co.paralleluniverse.fibers.Suspendable;
import com.modeln.flows.memberState.initiators.QueryOracle;
import com.modeln.services.Oracle;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatedBy;
import net.corda.core.utilities.ProgressTracker;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;

@InitiatedBy(QueryOracle.class)
public class QueryHandler extends FlowLogic<Void> {
    private static ProgressTracker.Step RECEIVING = new ProgressTracker.Step("Receiving query request.");
    private static ProgressTracker.Step CALCULATING = new ProgressTracker.Step("Calculating Nth prime.");
    private static ProgressTracker.Step SENDING = new ProgressTracker.Step("Sending query response.");

    private final ProgressTracker progressTracker = new ProgressTracker(RECEIVING, CALCULATING, SENDING);

    private final FlowSession session;

    @Nullable
    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    public QueryHandler(FlowSession session) {
        this.session = session;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {
        progressTracker.setCurrentStep(RECEIVING);
        progressTracker.setCurrentStep(CALCULATING);
        LinkedHashMap<String, String> response;
        LinkedHashMap<String, String> request = session.receive(LinkedHashMap.class).unwrap(it -> it);
        try {
            // Get the nth prime from the oracle.
            response = getServiceHub().cordaService(Oracle.class).query(request);
        } catch (Exception e) {
            // Re-throw the exception as a FlowException so its propagated to the querying node.
            throw new FlowException(e);
        }

        progressTracker.setCurrentStep(SENDING);
        session.send(response);
        System.out.println("Oracle Query Handler");
        return null;
    }
}
