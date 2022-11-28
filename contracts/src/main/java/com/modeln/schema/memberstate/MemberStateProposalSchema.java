package com.modeln.schema.memberstate;

import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import org.hibernate.annotations.Type;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.UUID;

public class MemberStateProposalSchema extends MappedSchema {

    public MemberStateProposalSchema() {
        super(MemberStateSchemaV1.class, 1, Arrays.asList(MemberStateProposalSchema.PersistMember.class));
    }

    @Entity
    @Table(name = "mn_member_state_proposal")
    public static class PersistMember extends PersistentState {

        @Column(name="linear_id") @Type(type = "uuid-char") private final UUID linearId;
        @Column(name="owner") private final Party owner;
        @Column(name="member_name") private final String memberName;
        @Column(name="memberType") private final String memberType;
        @Column(name="description") private final String description;
        @Column(name="DEAID") private final String DEAID;
        @Column(name="DDDID") private final String DDDID;
        @Column(name="member_status") private final String memberStatus;
        @Column(name="address") private final String address;
        @Column(name = "request_status") private final int status;
        @Column(name = "responder") private final Party responder;

        public PersistMember(UUID linearId, Party owner, Party responder, String memberName, String memberType,
                             String description, String DEAID, String DDDID, String memberStatus, String address, int status) {
            this.linearId = linearId;
            this.owner = owner;
            this.memberName = memberName;
            this.memberType = memberType;
            this.description = description;
            this.DEAID = DEAID;
            this.DDDID = DDDID;
            this.memberStatus = memberStatus;
            this.address = address;
            this.status = status;
            this.responder = responder;
        }

        public PersistMember() {
            this.linearId = null;
            this.owner = null;
            this.memberName = null;
            this.memberType = null;
            this.description = null;
            this.DEAID = null;
            this.DDDID = null;
            this.memberStatus = null;
            this.address = null;
            this.status = -1;
            this.responder = null;
        }

        public UUID getLinearId() {
            return linearId;
        }

        public Party getOwner() {
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

        public String getMemberStatus() {
            return memberStatus;
        }

        public String getAddress() {
            return address;
        }

        public int getStatus() {
            return status;
        }

        public Party getResponder() {
            return responder;
        }
    }
}
