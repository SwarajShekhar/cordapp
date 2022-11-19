package com.modeln.flows.bidaward.initiator;

import co.paralleluniverse.fibers.Suspendable;
import com.modeln.exceptions.RecordDoesNotExistException;
import com.modeln.states.bidawards.BidAwardState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@InitiatingFlow
@StartableByRPC
public class BroadcastBidAwardRequest extends FlowLogic<Void> {

    private UUID linearId;
    private List<AbstractParty> broadcastList;

    public BroadcastBidAwardRequest(UUID linearId, List<AbstractParty> broadcastList) {
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
        List<StateAndRef<BidAwardState>> bidAwardStateRefList = getServiceHub().getVaultService().queryBy(BidAwardState.class, inputCriteria).getStates();
        if(bidAwardStateRefList == null || bidAwardStateRefList.size() != 1)
            throw new RecordDoesNotExistException("Record for Bid Award State with linearID: " + linearId + " does not exist");
        StateAndRef<BidAwardState> bidAwardStateRef = bidAwardStateRefList.get(0);

        // Find the transaction that created this state.
        SecureHash creatingTransactionHash = bidAwardStateRef.getRef().getTxhash();
        SignedTransaction creatingTransaction = getServiceHub().getValidatedTransactions().getTransaction(creatingTransactionHash);

        // Send the transaction to the counterparty.
        for(AbstractParty party: broadcastList) {
            final FlowSession counterpartySession = initiateFlow(party);
            subFlow(new SendTransactionFlow(counterpartySession, creatingTransaction));
        }

        return null;
    }
}
