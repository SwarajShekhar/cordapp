package com.modeln.flows.membershipState.initiator;

import co.paralleluniverse.fibers.Suspendable;
import com.modeln.contracts.membershipstate.MemberShipStateContract;
import com.modeln.exceptions.RecordDoesNotExistException;
import com.modeln.states.membershipstate.MemberShipState;
import com.modeln.states.memberstate.MemberState;
import com.modeln.utils.FlowUtility;
import net.corda.core.contracts.LinearPointer;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

@InitiatingFlow
@StartableByRPC
public class AddMemberShipStateRequest extends FlowLogic<UniqueIdentifier> {

    private final String memberStateUUID;
    private final Instant startDate;
    private final Instant endDate;

    public AddMemberShipStateRequest(String memberStateUUID, Instant startDate, Instant endDate) {
        this.memberStateUUID = memberStateUUID;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    @Suspendable
    public UniqueIdentifier call() throws FlowException {

        // define parties
        final Party modeln = FlowUtility.getModelN(getServiceHub());
        final Party notary = FlowUtility.getNotary(getServiceHub());
        final Party oracle = getServiceHub().getNetworkMapCache().getPeerByLegalName(CordaX500Name.parse("O=Oracle,L=London,C=GB"));

        // verify if the object is present
        /*Boolean oracleResponse = subFlow(new ValidateLinearIdRequest(oracle, memberStateUUID));
        if(!oracleResponse)
            throw new RecordDoesNotExistException("Record with linearID: " + memberStateUUID + " does not exist");*/

        // create the object
        LinearPointer<MemberState> memberStateLinearPointer = new LinearPointer<>(
                new UniqueIdentifier(null, UUID.fromString(memberStateUUID)),
                MemberState.class
        );
        MemberShipState memberShipState = new MemberShipState(
                new UniqueIdentifier(), getOurIdentity(), modeln, memberStateLinearPointer, startDate, endDate, Instant.now());

        // create the transaction
        final TransactionBuilder builder = new TransactionBuilder(notary);
        builder.addOutputState(memberShipState);
        builder.addCommand(new MemberShipStateContract.Commands.Send(), Arrays.asList(getOurIdentity().getOwningKey(), modeln.getOwningKey() ));

        builder.verify(getServiceHub());
        final SignedTransaction ptx = getServiceHub().signInitialTransaction(builder);

        // initiate flow
        FlowSession session = initiateFlow(modeln);

        // collect signatures
        SignedTransaction stx = subFlow(new CollectSignaturesFlow(ptx, Arrays.asList(session)));
        subFlow(new FinalityFlow(stx, Arrays.asList(session)));

        return memberShipState.getLinearId();
    }
}
