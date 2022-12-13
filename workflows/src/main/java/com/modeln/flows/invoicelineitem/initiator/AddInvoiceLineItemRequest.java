package com.modeln.flows.invoicelineitem.initiator;

import co.paralleluniverse.fibers.Suspendable;
import com.modeln.contracts.bidawards.BidAwardStateContract;
import com.modeln.contracts.invoicelineitem.InvoiceLineItemStateContract;
import com.modeln.enums.invoicelineitem.Status;
import com.modeln.states.bidawards.BidAwardState;
import com.modeln.states.invoicelineitem.InvoiceLineItemState;
import com.modeln.states.memberstate.MemberState;
import com.modeln.utils.FlowUtility;
import net.corda.core.contracts.LinearPointer;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.time.Instant;
import java.util.Arrays;

@InitiatingFlow
@StartableByRPC
public class AddInvoiceLineItemRequest extends FlowLogic<UniqueIdentifier> {

    private final UniqueIdentifier memberStateUniqueIdentifier;
    private final String productNDC;
    private final String invoiceId;
    private final Instant invoiceDate;
    private final UniqueIdentifier bidAwardUniqueIdentifier;
    private final Party consumer;
    private final Status status;
    private final int quantity;

    public AddInvoiceLineItemRequest(UniqueIdentifier memberStateUniqueIdentifier, String productNDC,
                                     String invoiceId, Instant invoiceDate, UniqueIdentifier bidAwardUniqueIdentifier,
                                     Party consumer, Status status, int quantity) {
        this.memberStateUniqueIdentifier = memberStateUniqueIdentifier;
        this.productNDC = productNDC;
        this.invoiceId = invoiceId;
        this.invoiceDate = invoiceDate;
        this.bidAwardUniqueIdentifier = bidAwardUniqueIdentifier;
        this.consumer = consumer;
        this.status = status;
        this.quantity = quantity;

    }

    @Override
    @Suspendable
    public UniqueIdentifier call() throws FlowException {

        LinearPointer<MemberState> memberStateLinearPointer = new LinearPointer<>(
                new UniqueIdentifier(null, memberStateUniqueIdentifier.getId()),
                MemberState.class
        );

        LinearPointer<BidAwardState> bidAwardLinearPointer = new LinearPointer<>(
                new UniqueIdentifier(null, bidAwardUniqueIdentifier.getId()),
                BidAwardState.class
        );

        Party me = getOurIdentity();
        final Party notary = FlowUtility.getNotary(getServiceHub());
        UniqueIdentifier linearId = new UniqueIdentifier();

        InvoiceLineItemState invoiceLineItemState = new InvoiceLineItemState(
                me,
                this.consumer,
                memberStateLinearPointer,
                this.productNDC,
                this.invoiceId,
                this.invoiceDate,
                bidAwardLinearPointer,
                linearId,
                status,
                me,
                this.consumer,
                Instant.now(),
                this.quantity
        );

        final TransactionBuilder builder = new TransactionBuilder(notary);

        builder.addOutputState(invoiceLineItemState);
        builder.addCommand(new InvoiceLineItemStateContract.Commands.Request(),
                Arrays.asList(getOurIdentity().getOwningKey(), consumer.getOwningKey() ));
        builder.verify(getServiceHub());

        builder.verify(getServiceHub());
        final SignedTransaction ptx = getServiceHub().signInitialTransaction(builder);

        // initiate flow
        FlowSession session = initiateFlow(consumer);

        // collect signatures
        SignedTransaction stx = subFlow(new CollectSignaturesFlow(ptx, Arrays.asList(session)));
        subFlow(new FinalityFlow(stx, Arrays.asList(session)));

        return linearId;
    }
}
