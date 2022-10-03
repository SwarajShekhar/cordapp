package com.modeln.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.modeln.contracts.MemberStateContract;
import com.modeln.exceptions.RecordAlreadyExistsException;
import com.modeln.states.MemberState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ModelNAddMemberState {

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<UniqueIdentifier>{

        private Party proposer;
        private Party sender;
        private String memberName;
        private String memberType;
        private String description;
        private String DEAID;
        private String DDDID;
        private String status;

        public Initiator(String memberName, String memberType) {
            this.memberName = memberName;
            this.memberType = memberType;
        }

        @Override
        @Suspendable
        public UniqueIdentifier call() throws FlowException {
            //Hello World message
            this.proposer = getOurIdentity();
            this.sender = getServiceHub().getNetworkMapCache().getPeerByLegalName(CordaX500Name.parse("O=ModelN,L=London,C=GB"));

            //Compose the Member State
            final MemberState output = new MemberState(sender,new UniqueIdentifier(), memberName, memberType, description, DEAID, DDDID, status);

            // Check if the record exists
            QueryCriteria.VaultQueryCriteria inputCriteria = new QueryCriteria.VaultQueryCriteria()
                    .withStatus(Vault.StateStatus.UNCONSUMED)
                    .withRelevancyStatus(Vault.RelevancyStatus.RELEVANT);
            List<StateAndRef<MemberState>> memberStateRefList = getServiceHub().getVaultService().queryBy(MemberState.class).getStates();
            MemberState memberStateFromQuery = null;
            for(StateAndRef<MemberState> memberStateRef: memberStateRefList){
                memberStateFromQuery = (MemberState) memberStateRef.getState().getData();
                if(memberStateFromQuery.equals(output)){
                    throw new RecordAlreadyExistsException("Record Already Exists. " + output);
                }
            }

            // Step 1. Get a reference to the notary service on our network and our key pair.
            /** Explicit selection of notary by CordaX500Name - argument can by coded in flows or parsed from config (Preferred)*/
            final Party notary = getServiceHub().getNetworkMapCache().getNotary(CordaX500Name.parse("O=Notary,L=London,C=GB"));

            // Step 3. Create a new TransactionBuilder object.
            final TransactionBuilder builder = new TransactionBuilder(notary);

            // Step 4. Add the iou as an output state, as well as a command to the transaction builder.
            builder.addOutputState(output);
            builder.addCommand(new MemberStateContract.Commands.Send(), Arrays.asList(this.sender.getOwningKey()) );

            // Step 5. Verify and sign it with our KeyPair.
            builder.verify(getServiceHub());
            final SignedTransaction ptx = getServiceHub().signInitialTransaction(builder);

            // Step 6. Collect the other party's signature using the SignTransactionFlow.
            List<Party> otherParties = output.getParticipants().stream().map(el -> (Party)el).collect(Collectors.toList());
            otherParties.remove(getOurIdentity());
            List<FlowSession> sessions = otherParties.stream().map(el -> initiateFlow(el)).collect(Collectors.toList());

            SignedTransaction stx = subFlow(new CollectSignaturesFlow(ptx, sessions));

            // Step 7. Assuming no exceptions, we can now finalise the transaction
            subFlow(new FinalityFlow(stx, sessions));

            return output.getLinearId();
        }
    }

    @InitiatedBy(ModelNAddMemberState.Initiator.class)
    public static class TemplateFlowResponder extends FlowLogic<Void>{
        //private variable
        private FlowSession counterpartySession;

        //Constructor
        public TemplateFlowResponder(FlowSession counterpartySession) {
            this.counterpartySession = counterpartySession;
        }

        @Suspendable
        @Override
        public Void call() throws FlowException {
            SignedTransaction signedTransaction = subFlow(new SignTransactionFlow(counterpartySession) {
                @Suspendable
                @Override
                protected void checkTransaction(SignedTransaction stx) throws FlowException {
                    /*
                     * SignTransactionFlow will automatically verify the transaction and its signatures before signing it.
                     * However, just because a transaction is contractually valid doesn’t mean we necessarily want to sign.
                     * What if we don’t want to deal with the counterparty in question, or the value is too high,
                     * or we’re not happy with the transaction’s structure? checkTransaction
                     * allows us to define these additional checks. If any of these conditions are not met,
                     * we will not sign the transaction - even if the transaction and its signatures are contractually valid.
                     * ----------
                     * For this hello-world cordapp, we will not implement any aditional checks.
                     * */
                }
            });
            //Stored the transaction into data base.
            subFlow(new ReceiveFinalityFlow(counterpartySession, signedTransaction.getId()));
            return null;
        }
    }

}