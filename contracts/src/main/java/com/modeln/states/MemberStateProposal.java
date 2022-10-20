package com.modeln.states;

import com.modeln.contracts.MemberStateProposalContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@BelongsToContract(MemberStateProposalContract.class)
public class MemberStateProposal implements LinearState {

    private String name;
    private String type;
    private UniqueIdentifier linearId;
    private Party proposer;

    public MemberStateProposal(String name, String type, UniqueIdentifier linearId, Party proposer) {
        this.name = name;
        this.type = type;
        this.linearId = linearId;
        this.proposer = proposer;
    }

    public void setLinearId(UniqueIdentifier linearId) {
        this.linearId = linearId;
    }

    public Party getProposer() {
        return proposer;
    }

    public void setProposer(Party proposer) {
        this.proposer = proposer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(this.proposer);
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }
}
