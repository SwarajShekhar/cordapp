package com.modeln.contracts.memberstate;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;

public class MemberStateProposalContract implements Contract {

    public static final String ID = "com.modeln.contracts.memberstate.MemberStateProposalContract";

    @Override
    public void verify(LedgerTransaction tx) {

    }

    public interface Commands extends CommandData {
        class Send implements MemberStateProposalContract.Commands {}
    }
}
