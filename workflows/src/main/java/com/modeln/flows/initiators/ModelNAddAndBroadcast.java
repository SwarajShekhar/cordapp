package com.modeln.flows.initiators;

import co.paralleluniverse.fibers.Suspendable;
import com.modeln.exceptions.RecordDoesNotExistException;
import com.modeln.flows.ModelNBroadcastMemberState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;

import java.util.*;

public class ModelNAddAndBroadcast {
    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<UniqueIdentifier> {

        private final String name;
        private final String type;
        private final Party requester;

        public Initiator(String name, String type, Party requester) {
            this.name = name;
            this.type = type;
            this.requester = requester;
        }

        @Suspendable
        @Override
        public UniqueIdentifier call() throws FlowException {

            //check oracle if the data is duplicate?
            // check oracle for all other constraints
                //if all good   - Create
                // else         - throw exception
            //

            final Party oracle = getServiceHub().getNetworkMapCache().getPeerByLegalName(CordaX500Name.parse("O=Oracle,L=London,C=GB"));

            LinkedHashMap<String, String> oracleRequest = new LinkedHashMap<>();
            oracleRequest.put("name", name);
            oracleRequest.put("type", type);

            HashMap<String, String> oracleReply = subFlow(new QueryOracle(oracle, oracleRequest));

            UniqueIdentifier linearId = null;

            if("DEFAULT".equalsIgnoreCase(oracleReply.get("linearId"))){
                //throw new RecordDoesNotExistException("Record does not exist");
                linearId = subFlow(new ModelNAddMemberState.Initiator(name, type));
            }else {
                linearId = new UniqueIdentifier(null, UUID.fromString(oracleReply.get("linearId")));
            }

            subFlow(new ModelNBroadcastMemberState.Initiator(linearId.getId(), Arrays.asList(this.requester)));

            return linearId;
        }
    }
}
