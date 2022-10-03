package com.modeln.states;

import com.modeln.contracts.ThirdPartyMemberStateContract;
import com.modeln.schema.ThirdPartyMemberStateSchema;
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

import java.util.Arrays;
import java.util.List;

@BelongsToContract(ThirdPartyMemberStateContract.class)
public class ThirdPartyMemberState implements LinearState, QueryableState {

    private MemberState memberState;
    private Party receiver;
    private UniqueIdentifier linearId;
    private LinearPointer<MemberState> memberStateLinearPointer;
    private Party owner;

    @ConstructorForDeserialization
    public ThirdPartyMemberState(MemberState memberState, Party receiver, UniqueIdentifier linearId, LinearPointer<MemberState> memberStateLinearPointer, Party owner) {
        this.memberState = memberState;
        this.receiver = receiver;
        this.linearId = linearId;
        this.memberStateLinearPointer = memberStateLinearPointer;
        this.owner = owner;
    }

    public void setLinearId(UniqueIdentifier linearId) {
        this.linearId = linearId;
    }

    public Party getOwner() {
        return owner;
    }

    public void setOwner(Party owner) {
        this.owner = owner;
    }

    public LinearPointer<MemberState> getMemberStateLinearPointer() {
        return memberStateLinearPointer;
    }

    public void setMemberStateLinearPointer(LinearPointer<MemberState> memberStateLinearPointer) {
        this.memberStateLinearPointer = memberStateLinearPointer;
    }

    public ThirdPartyMemberState() {
    }

    public MemberState getMemberState() {
        return memberState;
    }

    public void setMemberState(MemberState memberState) {
        this.memberState = memberState;
    }

    public Party getReceiver() {
        return receiver;
    }

    public void setReceiver(Party receiver) {
        this.receiver = receiver;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(receiver, owner);
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    @NotNull
    @Override
    public PersistentState generateMappedObject(@NotNull MappedSchema schema) {
        if (schema instanceof ThirdPartyMemberStateSchema) {
            return new ThirdPartyMemberStateSchema.PersistMember(
                    this.owner.getName().toString(),
                    memberState.getMemberName(), memberState.getMemberType(), memberState.getDescription(),
                    memberState.getDEAID(), memberState.getDDDID(), memberState.getStatus(),
                    this.linearId.getId(), this.receiver.getName().toString());
        } else {
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }
    }

    @NotNull
    @Override
    public Iterable<MappedSchema> supportedSchemas() {
        return Arrays.asList(new ThirdPartyMemberStateSchema());
    }
}
