package com.modeln.schema.membershipstate;

import com.modeln.schema.memberstate.MemberStateSchemaV1;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import org.hibernate.annotations.Type;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

public class MemberShipStateSchema extends MappedSchema {
    public MemberShipStateSchema() {
        super(MemberStateSchemaV1.class, 1, Arrays.asList(com.modeln.schema.membershipstate.MemberShipStateSchema.PersistMember.class));
    }

    @Nullable
    @Override
    public String getMigrationResource() {
        return "iou.changelog-master";
    }

    @Entity
    @Table(name = "mn_member_ship_state")
    public static class PersistMember extends PersistentState {
        @Column(name = "owner") private final String owner;
        @Column(name = "receiver") private final String receiver;
        @Column(name = "linear_pointer") @Type(type = "uuid-char") private final UUID linearPointer;
        @Column(name = "linear_id") @Type(type = "uuid-char") private final UUID linearId;
        @Column(name = "start_date") private final Instant startDate;
        @Column(name = "end_date") private final Instant endDate;

        public PersistMember(String owner, String receiver, UUID linearPointer, UUID linearId, Instant startDate, Instant endDate) {
            this.owner = owner;
            this.receiver = receiver;
            this.linearPointer = linearPointer;
            this.linearId = linearId;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public String getOwner() {
            return owner;
        }

        public String getReceiver() {
            return receiver;
        }

        public UUID getLinearPointer() {
            return linearPointer;
        }

        public UUID getLinearId() {
            return linearId;
        }

        public Instant getStartDate() {
            return startDate;
        }

        public Instant getEndDate() {
            return endDate;
        }
    }
}
