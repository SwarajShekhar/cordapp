package com.modeln.schema.bidawards;

import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import org.hibernate.annotations.Type;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

public class BidAwardStateSchema extends MappedSchema {
    public BidAwardStateSchema() {
        super(BidAwardStateSchema.class, 1, Arrays.asList(BidAwardStateSchema.PersistMember.class));
    }

    @Entity
    @Table(name = "mn_bid_award")
    public static class PersistMember extends PersistentState {
        @Column(name = "owner") private final String owner;
        @Column(name = "member_state_linear_pointer") @Type(type = "uuid-char") private final UUID memberStateLinearPointer;
        @Column(name = "linear_id") @Type(type = "uuid-char") private final UUID linearId;
        @Column(name = "bid_award_id") private final String bidAwardId;
        @Column(name="product_ndc") private final String productNDC;
        @Column(name="wholesaler_id") private final String wholesalerId;
        @Column(name = "start_date") private final Instant startDate;
        @Column(name = "end_date") private final Instant endDate;
        @Column(name="wac_price") private final float wacPrice;
        @Column(name="authorized_price") private final float authorizedPrice;
        @Column(name = "event_date") private final Instant eventDate;

        public PersistMember(String owner, UUID memberStateLinearPointer, UUID linearId, String bidAwardId, String productNDC,
                             String wholesalerId, Instant startDate, Instant endDate, float wacPrice, float authorizedPrice,
                             Instant eventDate) {
            this.owner = owner;
            this.memberStateLinearPointer = memberStateLinearPointer;
            this.linearId = linearId;
            this.bidAwardId = bidAwardId;
            this.productNDC = productNDC;
            this.wholesalerId = wholesalerId;
            this.startDate = startDate;
            this.endDate = endDate;
            this.wacPrice = wacPrice;
            this.authorizedPrice = authorizedPrice;
            this.eventDate = eventDate;
        }

        public PersistMember() {
            this.owner = null;
            this.memberStateLinearPointer = null;
            this.linearId = null;
            this.bidAwardId = null;
            this.productNDC = null;
            this.wholesalerId = null;
            this.startDate = null;
            this.endDate = null;
            this.wacPrice = -1;
            this.authorizedPrice = -1;
            this.eventDate = null;
        }

        public String getOwner() {
            return owner;
        }

        public UUID getMemberStateLinearPointer() {
            return memberStateLinearPointer;
        }

        public UUID getLinearId() {
            return linearId;
        }

        public String getBidAwardId() {
            return bidAwardId;
        }

        public String getProductNDC() {
            return productNDC;
        }

        public String getWholesalerId() {
            return wholesalerId;
        }

        public Instant getStartDate() {
            return startDate;
        }

        public Instant getEndDate() {
            return endDate;
        }

        public float getWacPrice() {
            return wacPrice;
        }

        public float getAuthorizedPrice() {
            return authorizedPrice;
        }

        public Instant getEventDate() {
            return eventDate;
        }
    }
}
