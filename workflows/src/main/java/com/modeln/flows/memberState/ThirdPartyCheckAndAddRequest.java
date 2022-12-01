package com.modeln.flows.memberState;

import co.paralleluniverse.fibers.Suspendable;
import com.modeln.contracts.memberstate.ThirdPartyMemberStateContract;
import com.modeln.enums.memberstateproposal.MemberStateProposalStatus;
import com.modeln.exceptions.RecordDoesNotExistException;
import com.modeln.flows.memberState.initiators.AddMemberRequestProposal;
import com.modeln.flows.memberState.initiators.QueryOracle;
import com.modeln.flows.memberState.initiators.SignOracle;
import com.modeln.states.memberstate.MemberState;
import com.modeln.states.memberstate.ThirdPartyMemberState;
import net.corda.core.contracts.*;
import net.corda.core.crypto.TransactionSignature;
import net.corda.core.flows.*;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.transactions.FilteredTransaction;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.util.*;

public class ThirdPartyCheckAndAddRequest {
    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<Void> {

        private String memberName;
        private String memberType;
        private String description;
        private String DEAID;
        private String DDDID;
        private String memberStatus;
        private String address;
        private final MemberStateProposalStatus memberStateProposalStatus;

        public Initiator(String memberName, String memberType, String description,
                         String DEAID, String DDDID, String memberStatus, String address, MemberStateProposalStatus memberStateProposalStatus) {
            this.memberName = memberName;
            this.memberType = memberType;
            this.description = description;
            this.DEAID = DEAID;
            this.DDDID = DDDID;
            this.memberStatus = memberStatus;
            this.address = address;
            this.memberStateProposalStatus = memberStateProposalStatus;
        }

        @Suspendable
        @Override
        public Void call() throws FlowException {

            final Party notary = getServiceHub().getNetworkMapCache().getNotary(CordaX500Name.parse("O=Notary,L=London,C=GB"));

            final Party oracle = getServiceHub().getNetworkMapCache().getPeerByLegalName(CordaX500Name.parse("O=Oracle,L=London,C=GB"));

            ThirdPartyMemberState thirdPartyMemberState = null;

            // call oracle
            LinkedHashMap<String, String> oracleRequest = new LinkedHashMap<>();
            oracleRequest.put("name", memberName);
            oracleRequest.put("type", memberType);

            LinkedHashMap<String, String> oracleResponse = subFlow(new QueryOracle(oracle, oracleRequest));

            if(oracleResponse == null || oracleResponse.get("linearId") == null){
                subFlow(new AddMemberRequestProposal(
                        this.memberName,
                        this.memberType,
                        this.description,
                        this.DEAID,
                        this.DDDID,
                        this.memberStatus,
                        this.address,
                        this.memberStateProposalStatus
                ));
                oracleResponse = subFlow(new QueryOracle(oracle, oracleRequest));
            }
            if(oracleResponse == null || oracleResponse.get("linearId") == null){
                throw new RecordDoesNotExistException("Record doesnot exist");
            }
            thirdPartyMemberState = new ThirdPartyMemberState(new UniqueIdentifier(), new LinearPointer<>(
                    new UniqueIdentifier(null, UUID.fromString(oracleResponse.get("linearId"))),
                    MemberState.class
            ),
                    getOurIdentity());

            final TransactionBuilder builder = new TransactionBuilder(notary);
            builder.addOutputState(thirdPartyMemberState);
            builder.addCommand(new ThirdPartyMemberStateContract.Commands.Send(), Arrays.asList(getOurIdentity().getOwningKey(), oracle.getOwningKey() ));



            builder.verify(getServiceHub());

            final SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(builder);

            // get Oracle's signature
            FilteredTransaction ftx = partSignedTx.buildFilteredTransaction(o -> {
                return true;
            });
            TransactionSignature oracleSignature = subFlow(new SignOracle(oracle, ftx));
            SignedTransaction stx = partSignedTx.withAdditionalSignature(oracleSignature);

            subFlow(new FinalityFlow(stx, Collections.emptyList()));

            return null;
        }
    }

}