package com.modeln.flows.bidaward.initiator;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.AbstractParty;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@InitiatingFlow
@StartableByRPC
public class AddAndBroadcastBidAward extends FlowLogic<Void> {

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

    private List<AbstractParty> braodcastList;

    public AddAndBroadcastBidAward(String bidAwardId, UUID memberStateUUID, String productNDC, String wholesalerId, Instant startDate,
                                   float wacPrice, float authorizedPrice, Instant endDate, String wholesalerPartyName, List<AbstractParty> braodcastList) {
        this.bidAwardId = bidAwardId;
        this.memberStateUUID = memberStateUUID;
        this.productNDC = productNDC;
        this.wholesalerId = wholesalerId;
        this.startDate = startDate;
        this.wacPrice = wacPrice;
        this.authorizedPrice = authorizedPrice;
        this.endDate = endDate;
        this.wholesalerPartyName = wholesalerPartyName;
        this.braodcastList = braodcastList;
    }

    @Override
    @Suspendable
    public Void call() throws FlowException {
        // Add the bid award
        UniqueIdentifier uniqueIdentifier = subFlow(new AddBidAward(bidAwardId, memberStateUUID, productNDC, wholesalerId,
                startDate, wacPrice, authorizedPrice, endDate, wholesalerPartyName));

        // braodcast the bid award

        subFlow(new BroadcastBidAwardRequest(uniqueIdentifier.getId(), braodcastList));
        return null;
    }
}
