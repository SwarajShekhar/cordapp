package com.modeln.flows.memberState.initiators;

import co.paralleluniverse.fibers.Suspendable;
import com.modeln.contracts.memberstate.MemberStateContract;
import com.modeln.states.memberstate.MemberStateProposal;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.util.Arrays;

@InitiatingFlow
@StartableByRPC
public class ModelNAddMemberRequest extends FlowLogic<SignedTransaction> {


    private final String name;
    private final String type;

    public ModelNAddMemberRequest(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @Override
    @Suspendable
    public SignedTransaction call() throws FlowException {
        System.out.println(this.getClass().getSimpleName() + " --> Starting the call");
        final Party sender = getServiceHub().getNetworkMapCache().getPeerByLegalName(CordaX500Name.parse("O=ModelN,L=London,C=GB"));
        final MemberStateProposal output = new MemberStateProposal(name, type, new UniqueIdentifier(), getOurIdentity());
        final Party notary = getServiceHub().getNetworkMapCache().getNotary(CordaX500Name.parse("O=Notary,L=London,C=GB"));

        final TransactionBuilder builder = new TransactionBuilder(notary);
        builder.addOutputState(output);
        builder.addCommand(new MemberStateContract.Commands.Send(), sender.getOwningKey() );
        builder.verify(getServiceHub());

        final SignedTransaction ptx = getServiceHub().signInitialTransaction(builder);

        System.out.println(this.getClass().getSimpleName() + " --> transaction signed");
        FlowSession session = initiateFlow(sender);
        System.out.println(this.getClass().getSimpleName() + " --> transaction initiated");

        SignedTransaction stx = subFlow(new CollectSignaturesFlow(ptx, Arrays.asList(session)));

        System.out.println(this.getClass().getSimpleName() + " --> signature collection started");

        return subFlow(new FinalityFlow(stx, Arrays.asList(session)));
    }
}
