package com.edicius.meta;

import java.time.LocalDate;
import java.time.ZoneOffset;

public final class DailySeed {

    private DailySeed() {
    }

    public static long today() {
        LocalDate d = LocalDate.now(ZoneOffset.UTC);
        return d.getYear() * 10000L + d.getMonthValue() * 100L + d.getDayOfMonth();
    }
}
