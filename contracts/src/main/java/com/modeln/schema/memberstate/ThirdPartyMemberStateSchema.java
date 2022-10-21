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

public class ThirdPartyMemberStateSchema extends MappedSchema {

    public ThirdPartyMemberStateSchema() {
        super(ThirdPartyMemberStateSchemaV1.class, 1, Arrays.asList(ThirdPartyMemberStateSchema.PersistMember.class));
    }

    @Nullable
    @Override
    public String getMigrationResource() {
        return "iou.changelog-master";
    }

    @Entity
    @Table(name = "third_party_member_state")
    public static class PersistMember extends PersistentState {
        @Column(name = "owner") private final String owner;
        @Column(name = "linear_id") @Type(type = "uuid-char") private final UUID linearId;
        @Column(name = "linear_pointer") @Type(type = "uuid-char") private final UUID linearPointer;

        public PersistMember(String owner, UUID linearId, UUID linearPointer) {
            this.owner = owner;
            this.linearId = linearId;
            this.linearPointer = linearPointer;
        }

        public String getOwner() {
            return owner;
        }

        public UUID getLinearId() {
            return linearId;
        }

        public UUID getLinearPointer() {
            return linearPointer;
        }
    }
}
