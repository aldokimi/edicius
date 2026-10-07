package com.edicius.run;

public final class StyleRank {

    private StyleRank() {
    }

    public static String rank(float score) {
        if (score >= 80) return "S";
        if (score >= 55) return "A";
        if (score >= 35) return "B";
        if (score >= 15) return "C";
        return "D";
    }
}
