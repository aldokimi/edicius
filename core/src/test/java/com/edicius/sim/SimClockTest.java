package com.edicius.sim;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimClockTest {

    @Test
    void accumulateOneThirtiethYieldsTwoTicks() {
        SimClock clock = new SimClock();
        clock.accumulate(1f / 30f);
        assertTrue(clock.consumeTick());
        assertTrue(clock.consumeTick());
        assertFalse(clock.consumeTick());
        assertEquals(0f, clock.getAccumulator(), 1e-5f);
    }
}
