package com.modeln.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.modeln.contracts.ThirdPartyMemberStateContract;
import com.modeln.flows.initiators.ModelNAddMemberRequest;
import com.modeln.flows.initiators.QueryOracle;
import com.modeln.flows.initiators.SignOracle;
import com.modeln.states.MemberState;
import com.modeln.states.ThirdPartyMemberState;
import net.corda.core.contracts.*;
import net.corda.core.crypto.TransactionSignature;
import net.corda.core.flows.*;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.FilteredTransaction;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.util.*;

public class ThirdPartyCheckAndAddRequest {
    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<SignedTransaction> {

        private final String name;
        private final String type;

        public Initiator(String name, String type) {
            this.name = name;
            this.type = type;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {

            // Obtain a reference to a notary we wish to use.
            /** Explicit selection of notary by CordaX500Name - argument can by coded in flows or parsed from config (Preferred)*/
            final Party notary = getServiceHub().getNetworkMapCache().getNotary(CordaX500Name.parse("O=Notary,L=London,C=GB"));

            final Party otherParty = getServiceHub().getNetworkMapCache().getPeerByLegalName(CordaX500Name.parse("O=ModelN,L=London,C=GB"));

            final Party oracle = getServiceHub().getNetworkMapCache().getPeerByLegalName(CordaX500Name.parse("O=Oracle,L=London,C=GB"));

            Party me = getOurIdentity();

            LinkedHashMap<String, String> oracleRequest = new LinkedHashMap<>();
            oracleRequest.put("name", name);
            oracleRequest.put("type", type);

            LinkedHashMap<String, String> oracleReply = subFlow(new QueryOracle(oracle, oracleRequest));

            ThirdPartyMemberState thirdPartyMemberState = null;

            UniqueIdentifier linearId = null;

            System.out.println(this.getClass().getSimpleName() + " OracleReply: " + oracleReply);

            if("DEFAULT".equalsIgnoreCase(oracleReply.get("linearId"))){
                //linearId = subFlow(new ModelNAddMemberState.Initiator(name, type));
                System.out.println(this.getClass().getSimpleName() + " Trying to add member state request");
                subFlow(new ModelNAddMemberRequest(name, type));
            }
            oracleReply = subFlow(new QueryOracle(oracle, oracleRequest));
            if(!"DEFAULT".equalsIgnoreCase(oracleReply.get("linearId"))){
                LinearPointer<MemberState> memberStateLinearPointer = new LinearPointer<>(new UniqueIdentifier(null, UUID.fromString(oracleReply.get("linearId"))), MemberState.class);
                thirdPartyMemberState = new ThirdPartyMemberState(new UniqueIdentifier(), memberStateLinearPointer, getOurIdentity());
            }else{
                throw new IllegalArgumentException("Cannot create State");
            }


            // Generate an unsigned transaction.
            final Command<ThirdPartyMemberStateContract.Commands.Send> txCommand = new Command(
                    new ThirdPartyMemberStateContract.Commands.Send(),
                    Arrays.asList(getOurIdentity().getOwningKey()));
            final TransactionBuilder txBuilder = new TransactionBuilder(notary)
                    .addOutputState(thirdPartyMemberState, ThirdPartyMemberStateContract.ID)
                    .addCommand(txCommand);


            // Verify that the transaction is valid.
            txBuilder.verify(getServiceHub());

            // Sign the transaction.
            final SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(txBuilder);

            /*FilteredTransaction ftx = partSignedTx.buildFilteredTransaction(o -> {
                return true;
            });

            TransactionSignature oracleSignature = subFlow(new SignOracle(oracle, ftx));
            SignedTransaction stx = partSignedTx.withAdditionalSignature(oracleSignature);*/

            return subFlow(new FinalityFlow(partSignedTx, Collections.emptyList()));
        }
    }

}