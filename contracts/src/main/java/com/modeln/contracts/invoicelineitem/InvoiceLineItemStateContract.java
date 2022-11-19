package com.modeln.contracts.invoicelineitem;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

public class InvoiceLineItemStateContract implements Contract {

    public static final String ID = "com.modeln.contracts.invoicelineitem.InvoiceLineItemStateContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {

    }

    public interface Commands extends CommandData {
        class Send implements InvoiceLineItemStateContract.Commands {}
    }
}
