package com.modeln.states.invoicelineitem;

import com.modeln.contracts.invoicelineitem.InvoiceLineItemStateContract;
import com.modeln.enums.invoicelineitem.Status;
import com.modeln.schema.invoicelineitem.InvoiceLineItemStateSchema;
import com.modeln.states.bidawards.BidAwardState;
import com.modeln.states.memberstate.MemberState;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearPointer;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@BelongsToContract(InvoiceLineItemStateContract.class)
public class InvoiceLineItemState implements QueryableState, LinearState {

    private Party owner;
    private Party consumer;
    private LinearPointer<MemberState> memberStateLinearPointer;
    private String productNDC;
    private String invoiceId;
    private Instant invoiceDate;
    private LinearPointer<BidAwardState> bidAwardLinearPointer;
    private UniqueIdentifier linearId;

    // This get spopulated by Wholesaler asking for approval
    // Requires Approval    -   Wholesaler
    // Approved -   Manufacturer
    // Rejected -   Manufacturer
    private Status status;

    @ConstructorForDeserialization
    public InvoiceLineItemState(Party owner, Party consumer, LinearPointer<MemberState> memberStateLinearPointer, String productNDC, String invoiceId,
                                Instant invoiceDate, LinearPointer<BidAwardState> bidAwardLinearPointer, UniqueIdentifier linearId, Status status) {
        this.owner = owner;
        this.consumer = consumer;
        this.memberStateLinearPointer = memberStateLinearPointer;
        this.productNDC = productNDC;
        this.invoiceId = invoiceId;
        this.invoiceDate = invoiceDate;
        this.bidAwardLinearPointer = bidAwardLinearPointer;
        this.linearId = linearId;
        this.status = status;
    }

    public Party getOwner() {
        return owner;
    }

    public void setOwner(Party owner) {
        this.owner = owner;
    }

    public Party getConsumer() {
        return consumer;
    }

    public void setConsumer(Party consumer) {
        this.consumer = consumer;
    }

    public LinearPointer<MemberState> getMemberStateLinearPointer() {
        return memberStateLinearPointer;
    }

    public void setMemberStateLinearPointer(LinearPointer<MemberState> memberStateLinearPointer) {
        this.memberStateLinearPointer = memberStateLinearPointer;
    }

    public String getProductNDC() {
        return productNDC;
    }

    public void setProductNDC(String productNDC) {
        this.productNDC = productNDC;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Instant getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Instant invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public LinearPointer<BidAwardState> getBidAwardLinearPointer() {
        return bidAwardLinearPointer;
    }

    public void setBidAwardLinearPointer(LinearPointer<BidAwardState> bidAwardLinearPointer) {
        this.bidAwardLinearPointer = bidAwardLinearPointer;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setLinearId(UniqueIdentifier linearId) {
        this.linearId = linearId;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(this.owner, this.consumer);
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.linearId;
    }

    @NotNull
    @Override
    public PersistentState generateMappedObject(@NotNull MappedSchema schema) {
        if (schema instanceof InvoiceLineItemStateSchema) {
            return new InvoiceLineItemStateSchema.PersistMember(
                    this.owner.getName().toString(),
                    this.consumer.getName().toString(),
                    this.linearId.getId(),
                    this.memberStateLinearPointer.getPointer().getId(),
                    this.productNDC,
                    this.invoiceId,
                    this.invoiceDate,
                    this.bidAwardLinearPointer.getPointer().getId(),
                    status.ordinal()
            );
        } else {
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }
    }

    @NotNull
    @Override
    public Iterable<MappedSchema> supportedSchemas() {
        return Arrays.asList(new InvoiceLineItemStateSchema());
    }

}
