package com.modeln.flows.invoicelineitem.initiator;

import co.paralleluniverse.fibers.Suspendable;
import com.modeln.contracts.invoicelineitem.InvoiceLineItemStateContract;
import com.modeln.enums.invoicelineitem.Status;
import com.modeln.states.invoicelineitem.InvoiceLineItemState;
import com.modeln.utils.FlowUtility;

import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.util.Arrays;
import java.util.List;

@InitiatingFlow
@StartableByRPC
public class RespondToInvoiceLineItemRequest extends FlowLogic<UniqueIdentifier> {

    private final UniqueIdentifier invoiceLineItemIdentifier;
    private final Status status;

    public RespondToInvoiceLineItemRequest(UniqueIdentifier invoiceLineItemIdentifier, Status status) {
        this.invoiceLineItemIdentifier = invoiceLineItemIdentifier;
        this.status = status;
    }

    @Override
    @Suspendable
    public UniqueIdentifier call() throws FlowException {

        // Check if the record exists
        QueryCriteria.LinearStateQueryCriteria inputCriteria = new QueryCriteria.LinearStateQueryCriteria()
                .withStatus(Vault.StateStatus.UNCONSUMED)
                .withRelevancyStatus(Vault.RelevancyStatus.RELEVANT).withUuid(Arrays.asList(invoiceLineItemIdentifier.getId()));
        List<StateAndRef<InvoiceLineItemState>> invoiceLineItemStateRef =
                getServiceHub().getVaultService().queryBy(InvoiceLineItemState.class, inputCriteria).getStates();
        if(invoiceLineItemStateRef.size() != 1)
            throw new IllegalArgumentException("Invalid UUID: " + invoiceLineItemIdentifier.getId().toString() +
                    "\t Expected 1, but found: " + invoiceLineItemStateRef.size());

        // input object
        InvoiceLineItemState invoiceLineItemStateFromQuery =
                (InvoiceLineItemState) invoiceLineItemStateRef.get(0).getState().getData();

        // create output object
        InvoiceLineItemState output = new InvoiceLineItemState(
                getOurIdentity(),
                invoiceLineItemStateFromQuery.getOwner(),
                invoiceLineItemStateFromQuery.getMemberStateLinearPointer(),
                invoiceLineItemStateFromQuery.getProductNDC(),
                invoiceLineItemStateFromQuery.getInvoiceId(),
                invoiceLineItemStateFromQuery.getInvoiceDate(),
                invoiceLineItemStateFromQuery.getBidAwardLinearPointer(),
                this.invoiceLineItemIdentifier,
                this.status
        );

        // final Party notary = getServiceHub().getNetworkMapCache().getNotary(CordaX500Name.parse("O=Notary,L=London,C=GB"));
        final Party notary = FlowUtility.getNotary(getServiceHub());
        final TransactionBuilder builder = new TransactionBuilder(notary);

        builder.addOutputState(output).addInputState(invoiceLineItemStateRef.get(0));
        builder.addCommand(new InvoiceLineItemStateContract.Commands.Response(),
                Arrays.asList(getOurIdentity().getOwningKey(), invoiceLineItemStateFromQuery.getOwner().getOwningKey() ));
        builder.verify(getServiceHub());

        builder.verify(getServiceHub());
        final SignedTransaction ptx = getServiceHub().signInitialTransaction(builder);

        // initiate flow
        FlowSession session = initiateFlow(invoiceLineItemStateFromQuery.getOwner());

        // collect signatures
        SignedTransaction stx = subFlow(new CollectSignaturesFlow(ptx, Arrays.asList(session)));
        subFlow(new FinalityFlow(stx, Arrays.asList(session)));

        return invoiceLineItemIdentifier;
    }
}
