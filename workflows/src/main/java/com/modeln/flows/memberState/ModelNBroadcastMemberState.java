package com.modeln.flows.memberState;

import co.paralleluniverse.fibers.Suspendable;
import com.modeln.exceptions.RecordDoesNotExistException;
import com.modeln.states.memberstate.MemberState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.node.StatesToRecord;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ModelNBroadcastMemberState {

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<Void> {

        private UUID linearId;
        private List<AbstractParty> broadcastList;

        public Initiator(UUID linearId, List<AbstractParty> broadcastList) {
            this.linearId = linearId;
            this.broadcastList = broadcastList;
        }

        @Override
        @Suspendable
        public Void call() throws FlowException {
            QueryCriteria.LinearStateQueryCriteria inputCriteria = new QueryCriteria.LinearStateQueryCriteria()
                    .withStatus(Vault.StateStatus.UNCONSUMED)
                    .withUuid(Arrays.asList(linearId))
                    .withRelevancyStatus(Vault.RelevancyStatus.RELEVANT);
            List<StateAndRef<MemberState>> memberStateRefList = getServiceHub().getVaultService().queryBy(MemberState.class, inputCriteria).getStates();
            if(memberStateRefList == null || memberStateRefList.size() != 1)
                throw new RecordDoesNotExistException("Record for Member State with linearID: " + linearId + " does not exist");
            StateAndRef<MemberState> memberStateRef = memberStateRefList.get(0);

            // Find the transaction that created this state.
            SecureHash creatingTransactionHash = memberStateRef.getRef().getTxhash();
            SignedTransaction creatingTransaction = getServiceHub().getValidatedTransactions().getTransaction(creatingTransactionHash);

            // Send the transaction to the counterparty.
            for(AbstractParty party: broadcastList) {
                final FlowSession counterpartySession = initiateFlow(party);
                subFlow(new SendTransactionFlow(counterpartySession, creatingTransaction));
            }

            return null;
        }
    }

    @InitiatedBy(ModelNBroadcastMemberState.Initiator.class)
    public static class Responder extends FlowLogic<Void>{
        //private variable
        private FlowSession counterpartySession;

        //Constructor
        public Responder(FlowSession counterpartySession) {
            this.counterpartySession = counterpartySession;
        }

        @Suspendable
        @Override
        public Void call() throws FlowException {
            // Receive the transaction and store all its states.
            // If we don't pass `ALL_VISIBLE`, only the states for which the node is one of the `participants` will be stored.
            subFlow(new ReceiveTransactionFlow(counterpartySession, true, StatesToRecord.ALL_VISIBLE));

            return null;
        }
    }

}
