package com.modeln.contracts;

import com.modeln.states.memberstate.MemberState;
import org.junit.Test;

public class StateTests {

    //Mock State test check for if the state has correct parameters type
    @Test
    public void hasFieldOfCorrectType() throws NoSuchFieldException {
        MemberState.class.getDeclaredField("msg");
        assert (MemberState.class.getDeclaredField("msg").getType().equals(String.class));
    }
}