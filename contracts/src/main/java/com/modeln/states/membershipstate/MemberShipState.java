package com.modeln.states.membershipstate;

import com.modeln.contracts.membershipstate.MemberShipStateContract;
import com.modeln.schema.membershipstate.MemberShipStateSchema;
import com.modeln.schema.memberstate.MemberStateSchema;
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
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

@BelongsToContract(MemberShipStateContract.class)
public class MemberShipState implements LinearState, QueryableState {

    private UniqueIdentifier linearId;
    private Party owner;
    private Party receiver;
    private LinearPointer<MemberState> memberStateLinearPointer;
    private Timestamp startDate;
    private Timestamp endDate;


    public MemberShipState(UniqueIdentifier linearId, Party owner, Party receiver, LinearPointer<MemberState> memberStateLinearPointer, Timestamp startDate, Timestamp endDate) {
        this.linearId = linearId;
        this.owner = owner;
        this.receiver = receiver;
        this.memberStateLinearPointer = memberStateLinearPointer;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
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
                    this.endDate
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
