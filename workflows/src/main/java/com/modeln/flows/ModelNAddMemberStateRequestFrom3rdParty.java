package com.modeln.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.modeln.contracts.MemberStateContract;
import com.modeln.contracts.ThirdPartyMemberStateContract;
import com.modeln.exceptions.InvalidStateException;
import com.modeln.exceptions.RecordAlreadyExistsException;
import com.modeln.states.MemberState;
import com.modeln.states.ThirdPartyMemberState;
import net.corda.core.contracts.*;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelNAddMemberStateRequestFrom3rdParty {

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<SignedTransaction> {

        private final String name;
        private final String type;

        private final ProgressTracker.Step GENERATING_TRANSACTION = new ProgressTracker.Step("Generating transaction based on new Member.");
        private final ProgressTracker.Step VERIFYING_TRANSACTION = new ProgressTracker.Step("Verifying contract constraints.");
        private final ProgressTracker.Step SIGNING_TRANSACTION = new ProgressTracker.Step("Signing transaction with our private key.");
        private final ProgressTracker.Step GATHERING_SIGS = new ProgressTracker.Step("Gathering the counterparty's signature.") {
            @Override
            public ProgressTracker childProgressTracker() {
                return CollectSignaturesFlow.Companion.tracker();
            }
        };
        private final ProgressTracker.Step FINALISING_TRANSACTION = new ProgressTracker.Step("Obtaining notary signature and recording transaction.") {
            @Override
            public ProgressTracker childProgressTracker() {
                return FinalityFlow.Companion.tracker();
            }
        };

        // The progress tracker checkpoints each stage of the flow and outputs the specified messages when each
        // checkpoint is reached in the code. See the 'progressTracker.currentStep' expressions within the call()
        // function.
        private final ProgressTracker progressTracker = new ProgressTracker(
                GENERATING_TRANSACTION,
                VERIFYING_TRANSACTION,
                SIGNING_TRANSACTION,
                GATHERING_SIGS,
                FINALISING_TRANSACTION
        );

        public Initiator(String name, String type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public ProgressTracker getProgressTracker() {
            return progressTracker;
        }

        /**
         * The flow logic is encapsulated within the call() method.
         */
        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {

            // Obtain a reference to a notary we wish to use.
            /** Explicit selection of notary by CordaX500Name - argument can by coded in flows or parsed from config (Preferred)*/
            final Party notary = getServiceHub().getNetworkMapCache().getNotary(CordaX500Name.parse("O=Notary,L=London,C=GB"));

            final Party otherParty = getServiceHub().getNetworkMapCache().getPeerByLegalName(CordaX500Name.parse("O=ModelN,L=London,C=GB"));

            final Party oracle = getServiceHub().getNetworkMapCache().getPeerByLegalName(CordaX500Name.parse("O=Oracle,L=London,C=GB"));

            // Stage 1.
            progressTracker.setCurrentStep(GENERATING_TRANSACTION);
            // Generate an unsigned transaction.
            Party me = getOurIdentity();
            final MemberState memberState = new MemberState(otherParty,null, name, type, "description", "DEAID", "DDDID", "status");
            ThirdPartyMemberState thirdPartyMemberState = new ThirdPartyMemberState(memberState, otherParty,
                    new UniqueIdentifier(),
                    null, me);
            final Command<ThirdPartyMemberStateContract.Commands.Send> txCommand = new Command(
                    new ThirdPartyMemberStateContract.Commands.Send(),
                    Arrays.asList(me.getOwningKey(), otherParty.getOwningKey(), oracle.getOwningKey()));
            final TransactionBuilder txBuilder = new TransactionBuilder(notary)
                    .addOutputState(thirdPartyMemberState, ThirdPartyMemberStateContract.ID)
                    .addCommand(txCommand);

            // Stage 2.
            progressTracker.setCurrentStep(VERIFYING_TRANSACTION);
            // Verify that the transaction is valid.
            txBuilder.verify(getServiceHub());

            // Stage 3.
            progressTracker.setCurrentStep(SIGNING_TRANSACTION);
            // Sign the transaction.
            final SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(txBuilder);

            // Check Oracle and ask it to update Linear Pointer

            // Stage 4.
            progressTracker.setCurrentStep(GATHERING_SIGS);
            // Send the state to the counterparty, and receive it back with their signature.
            FlowSession otherPartySession = initiateFlow(otherParty);
            final SignedTransaction fullySignedTx = subFlow(
                    new CollectSignaturesFlow(partSignedTx, Arrays.asList(otherPartySession), CollectSignaturesFlow.Companion.tracker()));

            // Stage 5.
            progressTracker.setCurrentStep(FINALISING_TRANSACTION);
            // Notarise and record the transaction in both parties' vaults.
            return subFlow(new FinalityFlow(fullySignedTx, Arrays.asList(otherPartySession)));
        }
    }

    @InitiatedBy(ModelNAddMemberStateRequestFrom3rdParty.Initiator.class)
    public static class Responder extends FlowLogic<Void>{

        private FlowSession counterpartySession;
        private UniqueIdentifier linearId;


        public Responder(FlowSession counterpartySession) {
            this.counterpartySession = counterpartySession;
        }

        @Suspendable
        @Override
        public Void call() throws FlowException {
            SignedTransaction signedTransaction = subFlow(new SignTransactionFlow(counterpartySession) {
                @Suspendable
                @Override
                protected void checkTransaction(SignedTransaction stx) throws FlowException {
                    ContractState output = stx.getTx().getOutputs().get(0).getData();
                    if(output instanceof ThirdPartyMemberState){
                        List<AbstractParty> partyList = new ArrayList<>();
                        partyList.add(counterpartySession.getCounterparty());
                        MemberState memberState = (MemberState) ((ThirdPartyMemberState) output).getMemberState();
                        // Check if the record exists -- DONE BY ORACLE
                        QueryCriteria.VaultQueryCriteria inputCriteria = new QueryCriteria.VaultQueryCriteria()
                                .withStatus(Vault.StateStatus.UNCONSUMED)
                                .withRelevancyStatus(Vault.RelevancyStatus.RELEVANT);
                        List<StateAndRef<MemberState>> memberStateRefList = getServiceHub().getVaultService().queryBy(MemberState.class, inputCriteria).getStates();
                        MemberState memberStateFromQuery = null;
                        for(StateAndRef<MemberState> memberStateRef: memberStateRefList){
                            memberStateFromQuery = (MemberState) memberStateRef.getState().getData();
                            if(memberStateFromQuery.equals(memberState)){
                                linearId = memberStateFromQuery.getLinearId();
                                //counterpartySession.send(linearId);
                                // broadcast it to the party
                                subFlow(new ModelNBroadcastMemberState.Initiator(linearId.getId(), partyList));
                                return;
                            }
                        }
                        // create the record
                        linearId = subFlow(new ModelNAddMemberState.Initiator(memberState.getMemberName(), memberState.getMemberType()));
                        //counterpartySession.send(linearId);
                        // broadcast it to the party
                        subFlow(new ModelNBroadcastMemberState.Initiator(linearId.getId(), partyList));

                    }else{
                        throw new InvalidStateException("ContractState should be of type ThirdPartyMemberState instead of " + output.getClass().getSimpleName());
                    }
                }
            });

            subFlow(new ReceiveFinalityFlow(counterpartySession, signedTransaction.getId()));
            return null;
        }
    }

}
