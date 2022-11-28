package com.modeln.enums.memberstateproposal;

import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
public enum MemberStateProposalStatus {

    PROPOSED,
    APPROVED,
    REJECTED,
    CHANGED_AND_APPROVED

}
