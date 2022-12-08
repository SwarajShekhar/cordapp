package com.modeln.flows.membershipState.initiator;

import co.paralleluniverse.fibers.Suspendable;
import com.modeln.exceptions.RecordDoesNotExistException;
import com.modeln.states.membershipstate.MemberShipState;
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
public class BroadcastMembershipStateRequest extends FlowLogic<SignedTransaction> {

    private String linearId;
    private List<AbstractParty> partyList;

    public BroadcastMembershipStateRequest(String linearId, List<AbstractParty> partyList) {
        this.linearId = linearId;
        this.partyList = partyList;
    }

    @Override
    @Suspendable
    public SignedTransaction call() throws FlowException {
        QueryCriteria.LinearStateQueryCriteria inputCriteria = new QueryCriteria.LinearStateQueryCriteria()
                .withStatus(Vault.StateStatus.UNCONSUMED)
                .withUuid(Arrays.asList(UUID.fromString(linearId)))
                .withRelevancyStatus(Vault.RelevancyStatus.RELEVANT);
        List<StateAndRef<MemberShipState>> memberStateRefList = getServiceHub().getVaultService().queryBy(MemberShipState.class, inputCriteria).getStates();
        if(memberStateRefList == null || memberStateRefList.size() != 1)
            throw new RecordDoesNotExistException("Record for Membership State with linearID: " + linearId + " does not exist");
        StateAndRef<MemberShipState> memberStateRef = memberStateRefList.get(0);

        // Find the transaction that created this state.
        SecureHash creatingTransactionHash = memberStateRef.getRef().getTxhash();
        SignedTransaction creatingTransaction = getServiceHub().getValidatedTransactions().getTransaction(creatingTransactionHash);

        // Send the transaction to the counterparty.
        for(AbstractParty party: partyList) {
            final FlowSession counterpartySession = initiateFlow(party);
            subFlow(new SendTransactionFlow(counterpartySession, creatingTransaction));
        }

        return null;
    }

}
