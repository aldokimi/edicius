package com.edicius.gfx;

import com.badlogic.gdx.graphics.Color;

public enum ChromaPreset {
    CYAN("Cyan", 0.35f, 0.95f, 1f),
    MAGENTA("Magenta", 0.95f, 0.35f, 0.9f),
    GOLD("Gold", 1f, 0.85f, 0.25f),
    ACID("Acid", 0.55f, 1f, 0.35f),
    ICE("Ice", 0.65f, 0.85f, 1f),
    CRIMSON("Crimson", 1f, 0.3f, 0.35f);

    public final String label;
    public final Color tint;

    ChromaPreset(String label, float r, float g, float b) {
        this.label = label;
        this.tint = new Color(r, g, b, 1f);
    }

    public ChromaPreset next() {
        ChromaPreset[] v = values();
        return v[(ordinal() + 1) % v.length];
    }

    public ChromaPreset prev() {
        ChromaPreset[] v = values();
        return v[(ordinal() - 1 + v.length) % v.length];
    }

    public static ChromaPreset fromId(String id) {
        try {
            return valueOf(id);
        } catch (Exception e) {
            return CYAN;
        }
    }
}
