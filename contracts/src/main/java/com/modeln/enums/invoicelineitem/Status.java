package com.modeln.enums.invoicelineitem;

import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
public enum Status {
    APPROVAL_NEEDED, CANCEL_REQUESTED, APPROVED, REJECTED, CHANGE_REQUESTED
}
