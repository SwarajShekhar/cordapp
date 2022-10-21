package com.modeln.contracts.memberstate;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;

public class ThirdPartyMemberStateContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.modeln.contracts.memberstate.ThirdPartyMemberStateContract";

    @Override
    public void verify(LedgerTransaction tx) {

    }

    public interface Commands extends CommandData {
        class Send implements ThirdPartyMemberStateContract.Commands {}
    }

    public interface CommandCreate extends CommandData {
        class Create implements CommandData {

            public Create() {
                System.out.println("Inside create commandData");
            }

        }
    }
}
