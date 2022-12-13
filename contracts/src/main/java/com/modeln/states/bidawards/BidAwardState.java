package com.modeln.states.bidawards;

import com.modeln.contracts.bidawards.BidAwardStateContract;
import com.modeln.schema.bidawards.BidAwardStateSchema;
import com.modeln.states.memberstate.MemberState;
import net.corda.core.contracts.*;
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

@BelongsToContract(BidAwardStateContract.class)
public class BidAwardState implements OwnableState, LinearState, QueryableState {

    //pk - list
    private String bidAwardId;
    private LinearPointer<MemberState> memberStateLinearPointer;
    private String productNDC;
    private String wholesalerId;
    private Instant startDate;

    //updatable
    private float wacPrice;
    private float authorizedPrice;
    private Instant endDate;

    // get party from the name by peer...
    private String wholesalerPartyName;

    private UniqueIdentifier linearId;
    private Party owner;

    private Instant eventDate;

    @ConstructorForDeserialization
    public BidAwardState(String bidAwardId, LinearPointer<MemberState> memberStateLinearPointer, String productNDC, String wholesalerId, Instant startDate,
                         float wacPrice, float authorizedPrice, Instant endDate, String wholesalerPartyName, UniqueIdentifier linearId, Party owner,
                         Instant eventDate) {
        this.bidAwardId = bidAwardId;
        this.memberStateLinearPointer = memberStateLinearPointer;
        this.productNDC = productNDC;
        this.wholesalerId = wholesalerId;
        this.startDate = startDate;
        this.wacPrice = wacPrice;
        this.authorizedPrice = authorizedPrice;
        this.endDate = endDate;
        this.wholesalerPartyName = wholesalerPartyName;
        this.linearId = linearId;
        this.owner = owner;
        this.eventDate = eventDate;
    }

    public String getBidAwardId() {
        return bidAwardId;
    }

    public void setBidAwardId(String bidAwardId) {
        this.bidAwardId = bidAwardId;
    }

    public LinearPointer<MemberState> getMemberStateLinearPointer() {
        return memberStateLinearPointer;
    }

    public void setMemberStateLinearPointer(LinearPointer<MemberState> memberStateLinearPointer) {
        this.memberStateLinearPointer = memberStateLinearPointer;
    }

    public String getProductNDC() {
        return productNDC;
    }

    public void setProductNDC(String productNDC) {
        this.productNDC = productNDC;
    }

    public String getWholesalerId() {
        return wholesalerId;
    }

    public void setWholesalerId(String wholesalerId) {
        this.wholesalerId = wholesalerId;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public float getWacPrice() {
        return wacPrice;
    }

    public void setWacPrice(float wacPrice) {
        this.wacPrice = wacPrice;
    }

    public float getAuthorizedPrice() {
        return authorizedPrice;
    }

    public void setAuthorizedPrice(float authorizedPrice) {
        this.authorizedPrice = authorizedPrice;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public String getWholesalerPartyName() {
        return wholesalerPartyName;
    }

    public void setWholesalerPartyName(String wholesalerPartyName) {
        this.wholesalerPartyName = wholesalerPartyName;
    }

    public void setLinearId(UniqueIdentifier linearId) {
        this.linearId = linearId;
    }

    public void setOwner(Party owner) {
        this.owner = owner;
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
        if (schema instanceof BidAwardStateSchema) {
            return new BidAwardStateSchema.PersistMember(
                    this.owner.getName().toString(),
                    this.memberStateLinearPointer.getPointer().getId(),
                    this.linearId.getId(),
                    this.bidAwardId,
                    this.productNDC,
                    this.wholesalerId,
                    this.startDate,
                    this.endDate,
                    this.wacPrice,
                    this.authorizedPrice,
                    this.eventDate
            );
        } else {
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }
    }

    @NotNull
    @Override
    public Iterable<MappedSchema> supportedSchemas() {
        return Arrays.asList(new BidAwardStateSchema());
    }
}
