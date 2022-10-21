package com.modeln.states.memberstate;

import com.modeln.contracts.memberstate.MemberStateContract;
import com.modeln.schema.memberstate.MemberStateSchema;
import net.corda.core.contracts.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

// *********
// * State *
// *********
@BelongsToContract(MemberStateContract.class)
public class MemberState implements LinearState, OwnableState, QueryableState {

    private Party owner;
    private UniqueIdentifier linearId;
    private String memberName;
    private String memberType;
    private String description;
    private String DEAID;
    private String DDDID;
    private String status;

    @ConstructorForDeserialization
    public MemberState(Party owner, UniqueIdentifier linearId, String memberName, String memberType, String description, String DEAID, String DDDID, String status) {
        this.owner = owner;
        this.linearId = linearId;
        this.memberName = memberName;
        this.memberType = memberType;
        this.description = description;
        this.DEAID = DEAID;
        this.DDDID = DDDID;
        this.status = status;
    }

    public void setOwner(Party owner) {
        this.owner = owner;
    }

    public void setLinearId(UniqueIdentifier linearId) {
        this.linearId = linearId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(owner);
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    @NotNull
    @Override
    public AbstractParty getOwner() {
        return owner;
    }

    @NotNull
    @Override
    public CommandAndState withNewOwner(@NotNull AbstractParty newOwner) {
        return null;
    }

    @NotNull
    @Override
    public PersistentState generateMappedObject(@NotNull MappedSchema schema) {
        if (schema instanceof MemberStateSchema) {
            return new MemberStateSchema.PersistMember(
                    this.owner.getName().toString(),
                    memberName, memberType, description,
                    DEAID, DDDID, status,
                    this.linearId.getId());
        } else {
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }
    }

    @NotNull
    @Override
    public Iterable<MappedSchema> supportedSchemas() {
        return Arrays.asList(new MemberStateSchema());
    }

    @Override
    public boolean equals(Object object){
        if(object instanceof MemberState){
            MemberState memberState = (MemberState) object;
            return this.memberName.equals(memberState.getMemberName()) &&
                    this.memberType.equals(memberState.getMemberType());
        }
        return false;
    }

    @Override
    public String toString(){
        return "memberName: " + memberName + " AND memberType: " + memberType;
    }
}