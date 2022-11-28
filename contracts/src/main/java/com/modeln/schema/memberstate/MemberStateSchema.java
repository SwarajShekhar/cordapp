package com.modeln.schema.memberstate;

import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import org.hibernate.annotations.Type;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.UUID;

public class MemberStateSchema extends MappedSchema {

    public MemberStateSchema() {
        super(MemberStateSchemaV1.class, 1, Arrays.asList(MemberStateSchema.PersistMember.class));
    }

    @Nullable
    @Override
    public String getMigrationResource() {
        return "iou.changelog-master";
    }

    @Entity
    @Table(name = "mn_member_state")
    public static class PersistMember extends PersistentState {
        @Column(name = "owner") private final String owner;
        @Column(name = "member_name") private final String memberName;
        @Column(name = "member_type") private final String memberType;
        @Column(name = "description") private final String description;
        @Column(name = "deaid") private final String DEAID;
        @Column(name = "dddid") private final String DDDID;
        @Column(name = "status") private final String status;
        @Column(name = "linear_id") @Type(type = "uuid-char") private final UUID linearId;
        @Column(name = "address") private final String address;

        public PersistMember(String owner, String memberName, String memberType, String description,
                             String DEAID, String DDDID, String status, UUID linearId, String address) {
            this.owner = owner;
            this.memberName = memberName;
            this.memberType = memberType;
            this.description = description;
            this.DEAID = DEAID;
            this.DDDID = DDDID;
            this.status = status;
            this.linearId = linearId;
            this.address = address;
        }

        public String getOwner() {
            return owner;
        }

        public String getMemberName() {
            return memberName;
        }

        public String getMemberType() {
            return memberType;
        }

        public String getDescription() {
            return description;
        }

        public String getDEAID() {
            return DEAID;
        }

        public String getDDDID() {
            return DDDID;
        }

        public String getStatus() {
            return status;
        }

        public UUID getLinearId() {
            return linearId;
        }

        public String getAddress() {
            return address;
        }
    }
}
