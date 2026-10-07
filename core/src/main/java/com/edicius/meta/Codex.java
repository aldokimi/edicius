package com.edicius.meta;

import java.util.LinkedHashMap;
import java.util.Map;

public final class Codex {

    private Codex() {
    }

    public static Map<String, String> entries() {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("player", "Edicius — banned combat protocol in a borrowed shell.");
        m.put("nyx", "Nyx — handler, saboteur, maybe both. Trust the mission, audit the voice.");
        m.put("warden01", "Warden-01 — Boot Sector jailer. Telegraphs slams before it commits.");
        m.put("echo", "Echo Broker — Mirror District broker. Copies volleys when wounded.");
        m.put("gridroot", "GRID Root — the song at the spire. Phases, adds, no mercy.");
        m.put("style_a", "Style rank A+ — the city noticed your choreography.");
        return m;
    }
}
