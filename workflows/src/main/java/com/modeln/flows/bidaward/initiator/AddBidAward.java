package com.modeln.flows.bidaward.initiator;

import co.paralleluniverse.fibers.Suspendable;
import com.modeln.contracts.bidawards.BidAwardStateContract;
import com.modeln.states.bidawards.BidAwardState;
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
import java.util.Collections;
import java.util.UUID;

/*
Self initiating flow without a need for responder. This is for making sure data is in ledger.
 */
@InitiatingFlow
@StartableByRPC
public class AddBidAward extends FlowLogic<UniqueIdentifier> {


    private String bidAwardId;
    private UUID memberStateUUID;
    private String productNDC;
    private String wholesalerId;
    private Instant startDate;

    //updatable
    private float wacPrice;
    private float authorizedPrice;
    private Instant endDate;

    // get party from the name by peer...
    private String wholesalerPartyName;

    public AddBidAward(String bidAwardId, UUID memberStateUUID, String productNDC, String wholesalerId, Instant startDate,
                       float wacPrice, float authorizedPrice, Instant endDate, String wholesalerPartyName) {
        this.bidAwardId = bidAwardId;
        this.memberStateUUID = memberStateUUID;
        this.productNDC = productNDC;
        this.wholesalerId = wholesalerId;
        this.startDate = startDate;
        this.wacPrice = wacPrice;
        this.authorizedPrice = authorizedPrice;
        this.endDate = endDate;
        this.wholesalerPartyName = wholesalerPartyName;
    }

    @Override
    @Suspendable
    public UniqueIdentifier call() throws FlowException {

        LinearPointer<MemberState> memberStateLinearPointer = new LinearPointer<>(
                new UniqueIdentifier(null, memberStateUUID),
                MemberState.class
        );

        BidAwardState bidAwardState = new BidAwardState(
                this.bidAwardId,
                memberStateLinearPointer,
                this.productNDC,
                this.wholesalerId,
                this.startDate,
                this.wacPrice,
                this.authorizedPrice,
                this.endDate,
                this.wholesalerPartyName,
                new UniqueIdentifier(),
                getOurIdentity()

        );

        // final Party notary = getServiceHub().getNetworkMapCache().getNotary(CordaX500Name.parse("O=Notary,L=London,C=GB"));
        final Party notary = FlowUtility.getNotary(getServiceHub());
        final TransactionBuilder builder = new TransactionBuilder(notary);

        builder.addOutputState(bidAwardState);
        builder.addCommand(new BidAwardStateContract.Commands.Send(), getOurIdentity().getOwningKey() );
        builder.verify(getServiceHub());

        final SignedTransaction ptx = getServiceHub().signInitialTransaction(builder);

        subFlow(new FinalityFlow(ptx, Collections.emptyList()));

        return bidAwardState.getLinearId();
    }
}
