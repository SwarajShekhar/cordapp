package com.modeln.utils;

import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.node.NodeInfo;
import net.corda.core.node.ServiceHub;

import java.util.*;

public class FlowUtility {

    public static List<AbstractParty> getAllParty(ServiceHub serviceHub){
        Set<AbstractParty> partySet= new HashSet<>();
        List<NodeInfo> allNodes = serviceHub.getNetworkMapCache().getAllNodes();
        for(NodeInfo nodeInfo: allNodes){
            List<Party> parties = nodeInfo.getLegalIdentities();
            for(Party party: parties){
                partySet.add(party);
            }
        }
        return new ArrayList<>(partySet);
    }

    public static List<AbstractParty> getAllPartyForBroadcast(ServiceHub serviceHub, AbstractParty me){
        Set<AbstractParty> partySet= new HashSet<>();
        List<NodeInfo> allNodes = serviceHub.getNetworkMapCache().getAllNodes();
        for(NodeInfo nodeInfo: allNodes){
            List<Party> parties = nodeInfo.getLegalIdentities();
            for(Party party: parties){
                if(!(party.equals(me) || isNotary(serviceHub, party)))
                    partySet.add(party);
            }
        }
        return new ArrayList<>(partySet);
    }

    private static boolean isNotary(ServiceHub serviceHub, Party party){
        return serviceHub.getNetworkMapCache().isNotary(party);
    }

    public static Party getModelN(ServiceHub serviceHub){
        return serviceHub.getNetworkMapCache().getPeerByLegalName(CordaX500Name.parse("O=ModelN,L=London,C=GB,OU=modeln"));
    }

    public static Party getNotary(ServiceHub serviceHub){
        return serviceHub.getNetworkMapCache().getNotary(CordaX500Name.parse("O=Notary,L=London,C=GB,OU=notary"));
    }



}
