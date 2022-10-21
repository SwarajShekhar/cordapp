package com.modeln.contracts.membershipstate;

import com.modeln.contracts.memberstate.MemberStateContract;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

public class MemberShipStateContract implements Contract {

    public static final String ID = "com.modeln.contracts.membershipstate.MemberShipStateContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {

    }

    public interface Commands extends CommandData {
        class Send implements MemberStateContract.Commands {}
    }
}
