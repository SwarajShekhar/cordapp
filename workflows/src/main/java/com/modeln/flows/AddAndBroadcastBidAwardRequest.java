package com.modeln.flows;

import net.corda.core.identity.AbstractParty;

import java.util.List;

public class AddAndBroadcastBidAwardRequest {

    private String bidAwardId;
    private float wacPrice;
    private float authorizedPrice;
    private String customerId;
    private String productNDC;
    private String wholesalerId;
    // get party from the name by peer...
    private String wholesalerPartyName;

    private List<AbstractParty> broadcastingMembers;

}
