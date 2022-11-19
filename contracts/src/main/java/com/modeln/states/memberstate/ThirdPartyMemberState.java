package com.modeln.states.memberstate;

import com.modeln.contracts.memberstate.ThirdPartyMemberStateContract;
import com.modeln.schema.memberstate.ThirdPartyMemberStateSchema;
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

    private UniqueIdentifier linearId;
    private LinearPointer<MemberState> memberStateLinearPointer;
    private Party owner;

    @ConstructorForDeserialization
    public ThirdPartyMemberState(UniqueIdentifier linearId, LinearPointer<MemberState> memberStateLinearPointer, Party owner) {
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

    @NotNull
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
    public PersistentState generateMappedObject(@NotNull MappedSchema schema) {
        if (schema instanceof ThirdPartyMemberStateSchema) {
            return new ThirdPartyMemberStateSchema.PersistMember(
                    this.owner.getName().toString(), linearId.getId(),
                    memberStateLinearPointer.getPointer().getId()
                    );
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
