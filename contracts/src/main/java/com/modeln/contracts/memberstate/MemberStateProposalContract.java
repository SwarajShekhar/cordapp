package com.modeln.contracts.memberstate;

import com.modeln.enums.memberstateproposal.MemberStateProposalStatus;
import com.modeln.states.memberstate.MemberStateProposal;
import com.modeln.utils.ContractsUtil;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;

public class MemberStateProposalContract implements Contract {

    public static final String ID = "com.modeln.contracts.memberstate.MemberStateProposalContract";

    @Override
    public void verify(LedgerTransaction tx) {
        if(tx.getCommands().size() == 0){
            throw new IllegalArgumentException("One command Expected");
        }

        Command command = tx.getCommand(0);
        if(command.getValue() instanceof Commands.Send)
            verifyMemberStateProposalAddRequest(tx);
        else if(command.getValue() instanceof Commands.Respond)
            verifyMemberStateProposalRespondRequest(tx);
        else if(command.getValue() instanceof Commands.Add)
            verifyMemberStateProposalAddRequestFromWHoleOrManuf(tx);

    }

    public interface Commands extends CommandData {
        class Send implements MemberStateProposalContract.Commands {}
        class Respond implements MemberStateProposalContract.Commands {}
        class Add implements MemberStateProposalContract.Commands {}
    }

    private void verifyMemberStateProposalAddRequest(LedgerTransaction tx){
        MemberStateProposal memberStateProposalOutput = (MemberStateProposal) tx.getOutput(0);

        Party owner = memberStateProposalOutput.getOwner();
        if(! ((ContractsUtil.isGPO(owner) || ContractsUtil.isManufacturer(owner) || ContractsUtil.isWholesaler(owner)))
            &&
                ( memberStateProposalOutput.getMemberStateProposalStatus()== MemberStateProposalStatus.PROPOSED))
            throw new IllegalStateException("This party: " + owner + " is not supposed to initiate the call with status: " +
                    memberStateProposalOutput.getMemberStateProposalStatus().toString());
    }

    private void verifyMemberStateProposalRespondRequest(LedgerTransaction tx){
        MemberStateProposal memberStateProposalOutput = (MemberStateProposal) tx.getOutput(0);

        Party responder = memberStateProposalOutput.getResponder();
        if( !(ContractsUtil.isModelN(responder)) || memberStateProposalOutput.getMemberStateProposalStatus()==MemberStateProposalStatus.PROPOSED)
            throw new IllegalStateException("This party: " + responder + " is not supposed to initiate the call or the status: " +
                    memberStateProposalOutput.getMemberStateProposalStatus().toString() + " is wrong.");
    }

    private void verifyMemberStateProposalAddRequestFromWHoleOrManuf(LedgerTransaction tx){

    }
}
