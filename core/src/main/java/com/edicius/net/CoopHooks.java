package com.edicius.net;

/**
 * Future co-op: keep simulation driven by InputCommand lists per player id.
 * No networking yet — this documents the intended boundary.
 */
public final class CoopHooks {

    private CoopHooks() {
    }

    public static final int MAX_PLAYERS = 2;

    public static boolean isEnabled() {
        return false;
    }
}
