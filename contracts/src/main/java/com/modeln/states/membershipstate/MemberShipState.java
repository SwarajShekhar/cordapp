package com.modeln.states.membershipstate;

import com.modeln.contracts.membershipstate.MemberShipStateContract;
import com.modeln.schema.membershipstate.MemberShipStateSchema;
import com.modeln.states.memberstate.MemberState;
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

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@BelongsToContract(MemberShipStateContract.class)
public class MemberShipState implements LinearState, QueryableState {

    private UniqueIdentifier linearId;
    private Party owner;
    private Party receiver;
    private LinearPointer<MemberState> memberStateLinearPointer;
    private Instant startDate;
    private Instant endDate;
    private Instant eventDate;


    @ConstructorForDeserialization
    public MemberShipState(UniqueIdentifier linearId, Party owner, Party receiver, LinearPointer<MemberState> memberStateLinearPointer,
                           Instant startDate, Instant endDate, Instant eventDate) {
        this.linearId = linearId;
        this.owner = owner;
        this.receiver = receiver;
        this.memberStateLinearPointer = memberStateLinearPointer;
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventDate = eventDate;
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

    public Party getReceiver() {
        return receiver;
    }

    public void setReceiver(Party receiver) {
        this.receiver = receiver;
    }

    public LinearPointer<MemberState> getMemberStateLinearPointer() {
        return memberStateLinearPointer;
    }

    public void setMemberStateLinearPointer(LinearPointer<MemberState> memberStateLinearPointer) {
        this.memberStateLinearPointer = memberStateLinearPointer;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public Instant getEventDate() {
        return eventDate;
    }

    public void setEventDate(Instant eventDate) {
        this.eventDate = eventDate;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(owner, receiver);
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    @NotNull
    @Override
    public PersistentState generateMappedObject(@NotNull MappedSchema schema) {
        if (schema instanceof MemberShipStateSchema) {
            return new MemberShipStateSchema.PersistMember(
                    this.owner.getName().toString(),
                    this.receiver.getName().toString(),
                    this.memberStateLinearPointer.getPointer().getId(),
                    this.linearId.getId(),
                    this.startDate,
                    this.endDate,
                    this.eventDate
                    );
        } else {
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }
    }

    @NotNull
    @Override
    public Iterable<MappedSchema> supportedSchemas() {
        return Arrays.asList(new MemberShipStateSchema());
    }
}
