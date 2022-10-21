package com.modeln.flows.memberState.initiators;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.identity.Party;

import java.util.*;

@InitiatingFlow
public class QueryOracle extends FlowLogic<LinkedHashMap> {
    private final Party oracle;
    private final LinkedHashMap<String, String> n;

    public QueryOracle(Party oracle, LinkedHashMap<String, String> n) {
        this.oracle = oracle;
        this.n = n;
    }

    @Suspendable
    @Override
    public LinkedHashMap<String, String> call() throws FlowException {
        System.out.println("Oracle Query");
        return initiateFlow(oracle).sendAndReceive(LinkedHashMap.class, n).unwrap(it -> it);
    }
}
