package com.modeln.flows.memberState.initiators;

import co.paralleluniverse.fibers.Suspendable;
import com.modeln.contracts.memberstate.MemberStateProposalContract;
import com.modeln.enums.memberstateproposal.MemberStateProposalStatus;
import com.modeln.flows.memberState.ModelNBroadcastMemberState;
import com.modeln.flows.membershipState.initiator.AddMemberShipStateRequest;
import com.modeln.flows.membershipState.initiator.BroadcastMembershipStateRequest;
import com.modeln.states.memberstate.MemberState;
import com.modeln.states.memberstate.MemberStateProposal;
import com.modeln.utils.FlowUtility;
import net.corda.core.contracts.LinearPointer;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@InitiatingFlow
@StartableByRPC
public class RespondToAddMemberRequestProposalRequest extends FlowLogic<UniqueIdentifier> {

    private final UniqueIdentifier memberStateProposalIdentifier;
    private final String memberName;
    private final String memberType;
    private final String description;
    private final String DEAID;
    private final String DDDID;
    private final String memberStatus;
    private final String address;
    private final MemberStateProposalStatus memberStateProposalStatus;
    private final Instant startDate;
    private final Instant endDate;

    public RespondToAddMemberRequestProposalRequest(UniqueIdentifier memberStateProposalIdentifier, String memberName, String memberType,
                                                    String description, String DEAID, String DDDID, String memberStatus, String address,
                                                    MemberStateProposalStatus memberStateProposalStatus,
                                                    Instant startDate, Instant endDate) {
        this.memberStateProposalIdentifier = memberStateProposalIdentifier;
        this.memberName = memberName;
        this.memberType = memberType;
        this.description = description;
        this.DEAID = DEAID;
        this.DDDID = DDDID;
        this.memberStatus = memberStatus;
        this.address = address;
        this.memberStateProposalStatus = memberStateProposalStatus;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    @Suspendable
    public UniqueIdentifier call() throws FlowException {
        // Check if the record exists
        QueryCriteria.LinearStateQueryCriteria inputCriteria = new QueryCriteria.LinearStateQueryCriteria()
                .withStatus(Vault.StateStatus.UNCONSUMED)
                .withRelevancyStatus(Vault.RelevancyStatus.RELEVANT).withUuid(Arrays.asList(memberStateProposalIdentifier.getId()));
        List<StateAndRef<MemberStateProposal>> memberStateProposalStateRef =
                getServiceHub().getVaultService().queryBy(MemberStateProposal.class, inputCriteria).getStates();
        if(memberStateProposalStateRef.size() != 1)
            throw new IllegalArgumentException("Invalid UUID: " + memberStateProposalIdentifier.getId().toString() +
                    "\t Expected 1, but found: " + memberStateProposalStateRef.size());

        // input object
        MemberStateProposal memberStateProposalFromQuery =
                (MemberStateProposal) memberStateProposalStateRef.get(0).getState().getData();

        UniqueIdentifier uuid = null;
        if(memberStateProposalStatus == MemberStateProposalStatus.APPROVED ||
        memberStateProposalStatus == MemberStateProposalStatus.CHANGED_AND_APPROVED){
            uuid = createMemberState(memberStateProposalFromQuery);
            broadCastMemberState(uuid);
        }

        final Party notary = FlowUtility.getNotary(getServiceHub());

        final TransactionBuilder builder = new TransactionBuilder(notary);

        // create output object
        MemberStateProposal output = new MemberStateProposal(
                memberStateProposalIdentifier,
                getOurIdentity(),
                this.memberName,
                this.memberType,
                this.description,
                this.DEAID,
                this.DDDID,
                this.memberStatus,
                this.address,
                this.memberStateProposalStatus,
                memberStateProposalFromQuery.getOwner(),
                uuid == null ? null : new LinearPointer<>(uuid, MemberState.class),
                this.startDate,
                this.endDate,
                memberStateProposalFromQuery.getInternalName(),
                memberStateProposalFromQuery.getAdditionalInfo(),
                Instant.now()
        );

        builder.addOutputState(output).addInputState(memberStateProposalStateRef.get(0));
        builder.addCommand(new MemberStateProposalContract.Commands.Respond(),
                Arrays.asList(getOurIdentity().getOwningKey(), memberStateProposalFromQuery.getOwner().getOwningKey() ));
        builder.verify(getServiceHub());

        builder.verify(getServiceHub());
        final SignedTransaction ptx = getServiceHub().signInitialTransaction(builder);

        // initiate flow
        FlowSession session = initiateFlow(memberStateProposalFromQuery.getOwner());

        // collect signatures
        SignedTransaction stx = subFlow(new CollectSignaturesFlow(ptx, Arrays.asList(session)));
        subFlow(new FinalityFlow(stx, Arrays.asList(session)));

        return uuid;
    }

    @Suspendable
    private UniqueIdentifier createMemberState(MemberStateProposal memberStateProposalFromQuery) throws FlowException {
        UniqueIdentifier uuid =
                subFlow(new ModelNAddMemberState.Initiator(
                        this.memberName,
                        this.memberType,
                        this.description,
                        this.DEAID,
                        this.DDDID,
                        this.memberStatus,
                        this.address
                ));
        return uuid;
    }

    @Suspendable
    private void broadCastMemberState(UniqueIdentifier uuid) throws FlowException {
        //Get all peers for broadcasting
        List<AbstractParty> broadcastingMember = FlowUtility.getAllPartyForBroadcast(getServiceHub(), getOurIdentity());
        subFlow(new ModelNBroadcastMemberState.Initiator(uuid.getId(), broadcastingMember));
    }


}
