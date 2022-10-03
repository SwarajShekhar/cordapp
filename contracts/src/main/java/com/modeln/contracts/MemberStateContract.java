package com.modeln.contracts;

import com.modeln.states.MemberState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;

import static net.corda.core.contracts.ContractsDSL.requireThat;

// ************
// * Contract *
// ************
public class MemberStateContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.modeln.contracts.MemberStateContract";

    @Override
    public void verify(LedgerTransaction tx) {

    }

    public interface Commands extends CommandData {
        class Send implements Commands {}
    }
}