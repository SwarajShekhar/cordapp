package com.modeln.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.identity.Party;

@InitiatingFlow
public class QueryForOracle {


    public class QueryPrime extends FlowLogic<String> {
        private final Party oracle;
        private final String str;

        public QueryPrime(Party oracle, String str) {
            this.oracle = oracle;
            this.str = str;
        }

        @Suspendable
        @Override
        public String call() throws FlowException {
            return initiateFlow(oracle).sendAndReceive(String.class, str).unwrap(it -> it);
        }
    }
}
