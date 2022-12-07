package com.modeln.contracts.invoicelineitem;

import com.modeln.enums.invoicelineitem.Status;
import com.modeln.states.invoicelineitem.InvoiceLineItemState;
import com.modeln.utils.ContractsUtil;

import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

public class InvoiceLineItemStateContract implements Contract {

    public static final String ID = "com.modeln.contracts.invoicelineitem.InvoiceLineItemStateContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        if(tx.getCommands().size() == 0){
            throw new IllegalArgumentException("One command Expected");
        }

        Command command = tx.getCommand(0);
        if(command.getValue() instanceof Commands.Request)
            verifyInvoiceLineItemStateContractRequest(tx);
        else if(command.getValue() instanceof Commands.Response)
            verifyInvoiceLineItemStateContractResponse(tx);
    }

    private void verifyInvoiceLineItemStateContractRequest(LedgerTransaction tx){
        Command command = tx.getCommand(0);
        InvoiceLineItemState auctionState = (InvoiceLineItemState) tx.getOutput(0);

        if(!(ContractsUtil.isWholesaler(auctionState.getOwner())
            && (auctionState.getStatus() == Status.APPROVAL_NEEDED || auctionState.getStatus() == Status.CANCEL_REQUESTED))){
            throw new IllegalArgumentException("Only Wholesaler is allowed to perfrom this action");
        }
    }

    private void verifyInvoiceLineItemStateContractResponse(LedgerTransaction tx){
        Command command = tx.getCommand(0);
        InvoiceLineItemState auctionStateOutput = (InvoiceLineItemState) tx.getOutput(0);

        if(ContractsUtil.isWholesaler(auctionStateOutput.getOwner())
                && (!(auctionStateOutput.getStatus() == Status.CANCEL_REQUESTED))){
            throw new IllegalArgumentException("You are not allowed to perfrom this action");
        }

        InvoiceLineItemState auctionStateInput = (InvoiceLineItemState) tx.getInput(0);
        if(!(ContractsUtil.isWholesaler(auctionStateInput.getOwner())
                && (auctionStateInput.getStatus() == Status.APPROVAL_NEEDED))){
            throw new IllegalArgumentException("Invalid Input");
        }

        if(auctionStateOutput.getStatus() == Status.APPROVAL_NEEDED || auctionStateOutput.getStatus() == Status.CANCEL_REQUESTED)
            throw new IllegalArgumentException("You are not supposed to ask for approval or cancel the request");
    }

    public interface Commands extends CommandData {
        class Request implements InvoiceLineItemStateContract.Commands {}
        class Response implements InvoiceLineItemStateContract.Commands {}
    }
}
