package com.modeln.states.memberstate;

import com.modeln.contracts.memberstate.MemberStateProposalContract;
import com.modeln.enums.memberstateproposal.MemberStateProposalStatus;
import com.modeln.schema.memberstate.MemberStateProposalSchema;
import net.corda.core.contracts.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@BelongsToContract(MemberStateProposalContract.class)
public class MemberStateProposal implements LinearState, QueryableState {

    private UniqueIdentifier linearId;
    private Party owner;
    private Party responder;
    private String memberName;
    private String memberType;
    private String description;
    private String DEAID;
    private String DDDID;
    private String memberStatus;
    private String address;
    private MemberStateProposalStatus memberStateProposalStatus;

    public MemberStateProposal(UniqueIdentifier linearId, Party owner, String memberName, String memberType, String description,
                               String DEAID, String DDDID, String memberStatus, String address,
                               MemberStateProposalStatus memberStateProposalStatus, Party responder) {
        this.linearId = linearId;
        this.owner = owner;
        this.memberName = memberName;
        this.memberType = memberType;
        this.description = description;
        this.DEAID = DEAID;
        this.DDDID = DDDID;
        this.memberStatus = memberStatus;
        this.address = address;
        this.memberStateProposalStatus = memberStateProposalStatus;
        this.responder = responder;
    }

    public Party getResponder() {
        return responder;
    }

    public void setResponder(Party responder) {
        this.responder = responder;
    }

    public MemberStateProposalStatus getMemberStateProposalStatus() {
        return memberStateProposalStatus;
    }

    public void setMemberStateProposalStatus(MemberStateProposalStatus memberStateProposalStatus) {
        this.memberStateProposalStatus = memberStateProposalStatus;
    }

    public void setLinearId(UniqueIdentifier linearId) {
        this.linearId = linearId;
    }

    public void setOwner(Party owner) {
        this.owner = owner;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDEAID() {
        return DEAID;
    }

    public void setDEAID(String DEAID) {
        this.DEAID = DEAID;
    }

    public String getDDDID() {
        return DDDID;
    }

    public void setDDDID(String DDDID) {
        this.DDDID = DDDID;
    }

    public String getMemberStatus() {
        return memberStatus;
    }

    public void setMemberStatus(String memberStatus) {
        this.memberStatus = memberStatus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(this.owner, this.responder);
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.linearId;
    }

    public Party getOwner() {
        return this.owner;
    }

    @NotNull
    @Override
    public PersistentState generateMappedObject(@NotNull MappedSchema schema) {
        if (schema instanceof MemberStateProposalSchema) {
            return new MemberStateProposalSchema.PersistMember(
                    this.linearId.getId(),
                    this.owner,
                    this.responder,
                    this.memberName,
                    this.memberType,
                    this.description,
                    this.DEAID,
                    this.DDDID,
                    this.memberStatus,
                    this.address,
                    this.memberStateProposalStatus.ordinal());
        } else {
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }
    }

    @NotNull
    @Override
    public Iterable<MappedSchema> supportedSchemas() {
        return Arrays.asList(new MemberStateProposalSchema());
    }
}
