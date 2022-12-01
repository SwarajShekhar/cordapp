package com.modeln.contracts.memberstate;

import com.modeln.enums.memberstateproposal.MemberStateProposalStatus;
import com.modeln.states.memberstate.MemberState;
import com.modeln.states.memberstate.MemberStateProposal;
import com.modeln.utils.ContractsUtil;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;

// ************
// * Contract *
// ************
public class MemberStateContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.modeln.contracts.memberstate.MemberStateContract";

    @Override
    public void verify(LedgerTransaction tx) {
        if(tx.getCommands().size() == 0){
            throw new IllegalArgumentException("One command Expected");
        }

        Command command = tx.getCommand(0);
        if(command.getValue() instanceof MemberStateProposalContract.Commands.Send)
            verifyMemberStateAddition(tx);
    }

    public interface Commands extends CommandData {
        class Approve implements Commands {}
    }

    private void verifyMemberStateAddition(LedgerTransaction tx){
        MemberState memberStateOutput = (MemberState) tx.getOutput(0);

    }
}