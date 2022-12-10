package com.modeln.flows.memberState.initiators;

import co.paralleluniverse.fibers.Suspendable;
import com.modeln.contracts.memberstate.MemberStateProposalContract;
import com.modeln.enums.memberstateproposal.MemberStateProposalStatus;
import com.modeln.states.memberstate.MemberStateProposal;
import com.modeln.utils.FlowUtility;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.time.Instant;
import java.util.Arrays;

@InitiatingFlow
@StartableByRPC
public class AddMemberRequestProposal extends FlowLogic<UniqueIdentifier> {


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
    private String internalName;
    private String additionalInfo;

    public AddMemberRequestProposal(String memberName, String memberType, String description,
                                    String DEAID, String DDDID, String memberStatus, String address,
                                    MemberStateProposalStatus memberStateProposalStatus, Instant startDate, Instant endDate,
                                    String internalName, String additionalInfo) {
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
        this.internalName = internalName;
        this.additionalInfo = additionalInfo;
    }

    @Override
    @Suspendable
    public UniqueIdentifier call() throws FlowException {
        final Party sender = FlowUtility.getModelN(getServiceHub());
        UniqueIdentifier uuid = new UniqueIdentifier();
        final MemberStateProposal output = new MemberStateProposal(
                uuid,
                getOurIdentity(),
                this.memberName,
                this.memberType,
                this.description,
                this.DEAID,
                this.DDDID,
                this.memberStatus,
                this.address,
                this.memberStateProposalStatus,
                sender,
                null,
                this.startDate,
                this.endDate,
                this.internalName,
                this.additionalInfo
        );

        final Party notary = FlowUtility.getNotary(getServiceHub());

        final TransactionBuilder builder = new TransactionBuilder(notary);
        builder.addOutputState(output);
        builder.addCommand(new MemberStateProposalContract.Commands.Send(), sender.getOwningKey() );
        builder.verify(getServiceHub());

        final SignedTransaction ptx = getServiceHub().signInitialTransaction(builder);
        FlowSession session = initiateFlow(sender);
        SignedTransaction stx = subFlow(new CollectSignaturesFlow(ptx, Arrays.asList(session)));
        subFlow(new FinalityFlow(stx, Arrays.asList(session)));

        return uuid;
    }
}
