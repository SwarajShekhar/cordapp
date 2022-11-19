package com.modeln.flows.memberState.responders;

import co.paralleluniverse.fibers.Suspendable;
import com.modeln.exceptions.InvalidStateException;
import com.modeln.flows.memberState.ModelNBroadcastMemberState;
import com.modeln.flows.memberState.initiators.ModelNAddMemberRequest;
import com.modeln.flows.memberState.initiators.ModelNAddMemberState;
import com.modeln.states.memberstate.MemberStateProposal;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;

import java.util.Arrays;


@InitiatedBy(ModelNAddMemberRequest.class)
public class ModelNAddMemberResponse extends FlowLogic<Void> {

    private FlowSession counterpartySession;
    public ModelNAddMemberResponse(FlowSession counterpartySession) {
        this.counterpartySession = counterpartySession;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {
        SignedTransaction signedTransaction = subFlow(new SignTransactionFlow(counterpartySession) {
            @Suspendable
            @Override
            protected void checkTransaction(SignedTransaction stx) throws FlowException {
                ContractState contractState = stx.getTx().getOutputs().get(0).getData();
                if(contractState instanceof MemberStateProposal) {
                    /**
                     * TO-DO: User Interaction for approval
                     */
                    MemberStateProposal proposal = (MemberStateProposal)contractState;
                    System.out.println(this.getClass().getSimpleName() + " --> subflow starting next for Addmember");
                    UniqueIdentifier uuid = subFlow(new ModelNAddMemberState.Initiator(((MemberStateProposal) contractState).getName(), ((MemberStateProposal) contractState).getType()));
                    System.out.println(this.getClass().getSimpleName() + " --> linearID: " + uuid);
                    // broadcast now
                    Party broadcastingMember = ((MemberStateProposal) contractState).getProposer();
                    subFlow(new ModelNBroadcastMemberState.Initiator(uuid.getId(), Arrays.asList(broadcastingMember)));
                }else{
                    throw new InvalidStateException("Invalid contractState");
                }
            }
        });
        subFlow(new ReceiveFinalityFlow(counterpartySession, signedTransaction.getId()));
        return null;
    }
}
