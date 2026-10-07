package com.edicius.run;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StyleRankTest {

    @Test
    void ranksThresholds() {
        assertEquals("D", StyleRank.rank(0));
        assertEquals("C", StyleRank.rank(15));
        assertEquals("B", StyleRank.rank(35));
        assertEquals("A", StyleRank.rank(55));
        assertEquals("S", StyleRank.rank(80));
    }
}
