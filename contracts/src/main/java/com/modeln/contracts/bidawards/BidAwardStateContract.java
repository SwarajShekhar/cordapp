package com.modeln.contracts.bidawards;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

public class BidAwardStateContract implements Contract {

    public static final String ID = "com.modeln.contracts.bidawards.BidAwardStateContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {

    }

    public interface Commands extends CommandData {
        class Send implements BidAwardStateContract.Commands {}
    }

}
