package com.modeln.services;

import com.modeln.utils.QueryPG;
import net.corda.core.crypto.TransactionSignature;
import net.corda.core.node.ServiceHub;
import net.corda.core.node.services.CordaService;
import net.corda.core.serialization.SingletonSerializeAsToken;
import net.corda.core.transactions.ComponentVisibilityException;
import net.corda.core.transactions.FilteredTransaction;
import net.corda.core.transactions.FilteredTransactionVerificationException;

import java.security.PublicKey;
import java.util.LinkedHashMap;

@CordaService
public class Oracle extends SingletonSerializeAsToken {

    private ServiceHub services;
    private final PublicKey myKey;

    public Oracle(ServiceHub services) {
        this.services = services;
        this.myKey = services.getMyInfo().getLegalIdentities().get(0).getOwningKey();
    }

    public TransactionSignature sign(FilteredTransaction ftx) throws FilteredTransactionVerificationException {
        // Check the partial Merkle tree is valid.
        ftx.verify();

        // Is it a Merkle tree we are willing to sign over?
        boolean isValidMerkleTree = ftx.checkWithFun(this::isCorrect);
        try {
            /**
             * Function that checks if all of the commands that should be signed by the input public key are visible.
             * This functionality is required from Oracles to check that all of the commands they should sign are visible.
             */
            ftx.checkCommandVisibility(services.getMyInfo().getLegalIdentities().get(0).getOwningKey());
        } catch (ComponentVisibilityException e) {
            e.printStackTrace();
        }

        if (isValidMerkleTree) {
            return services.createSignature(ftx, myKey);
        } else {
            throw new IllegalArgumentException("Oracle signature requested over invalid transaction.");
        }
    }

    private boolean isCorrect(Object o){
        return true;
    }

    public LinkedHashMap<String, String> query(LinkedHashMap<String, String> map) {
        String value = QueryPG.getLinearId(map.get("name"), map.get("type"));
        LinkedHashMap<String, String> response = new LinkedHashMap<>();
        response.put("linearId", value);
        return response;
    }

    public boolean isLinerIdvalid(String linearId) {
        return QueryPG.isLinearId(linearId);
    }
}
