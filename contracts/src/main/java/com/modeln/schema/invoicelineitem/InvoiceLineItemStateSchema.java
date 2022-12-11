package com.modeln.schema.invoicelineitem;

import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

public class InvoiceLineItemStateSchema extends MappedSchema {

    public InvoiceLineItemStateSchema() {
        super(InvoiceLineItemStateSchema.class, 1, Arrays.asList(InvoiceLineItemStateSchema.PersistMember.class));
    }

    @Entity
    @Table(name = "mn_bid_award")
    public static class PersistMember extends PersistentState {
        @Column(name = "owner") private final String owner;
        @Column(name = "consumer") private final String consumer;
        @Column(name = "linear_id") @Type(type = "uuid-char") private final UUID linearId;
        @Column(name = "member_state_linear_pointer") @Type(type = "uuid-char") private final UUID memberStateLinearPointer;
        @Column(name="product_ndc") private final String productNDC;
        @Column(name="invoice_id") private final String invoiceId;
        @Column(name="invoice_date") private final Instant invoiceDate;
        @Column(name = "bid_award_linear_pointer") @Type(type = "uuid-char")private final UUID bidAwardLinearPointer;
        @Column(name = "current_status") private final int status;
        @Column(name = "wholesaler") private final String wholesaler;
        @Column(name = "manufacturer") private final String manufacturer;

        public PersistMember(){
            owner = null;
            consumer = null;
            linearId = null;
            memberStateLinearPointer = null;
            productNDC = null;
            invoiceId = null;
            invoiceDate = null;
            bidAwardLinearPointer = null;
            status = -1;
            wholesaler = null;
            manufacturer = null;
        }

        public PersistMember(String owner, String consumer, UUID linearId, UUID memberStateLinearPointer, String productNDC, String invoiceId,
                             Instant invoiceDate, UUID bidAwardLinearPointer, int status, String wholesaler, String manufacturer) {
            this.owner = owner;
            this.consumer = consumer;
            this.linearId = linearId;
            this.memberStateLinearPointer = memberStateLinearPointer;
            this.productNDC = productNDC;
            this.invoiceId = invoiceId;
            this.invoiceDate = invoiceDate;
            this.bidAwardLinearPointer = bidAwardLinearPointer;
            this.status = status;
            this.wholesaler = wholesaler;
            this.manufacturer = manufacturer;
        }

        public String getConsumer() {
            return consumer;
        }

        public UUID getLinearId() {
            return linearId;
        }

        public String getOwner() {
            return owner;
        }

        public UUID getMemberStateLinearPointer() {
            return memberStateLinearPointer;
        }

        public String getProductNDC() {
            return productNDC;
        }

        public String getInvoiceId() {
            return invoiceId;
        }

        public Instant getInvoiceDate() {
            return invoiceDate;
        }

        public UUID getBidAwardLinearPointer() {
            return bidAwardLinearPointer;
        }

        public int getStatus() {
            return status;
        }

        public String getWholesaler() {
            return wholesaler;
        }

        public String getManufacturer() {
            return manufacturer;
        }
    }
}
