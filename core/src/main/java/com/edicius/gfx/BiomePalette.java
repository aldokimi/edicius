package com.edicius.gfx;

import com.badlogic.gdx.graphics.Color;

public enum BiomePalette {
    BOOT(
            "Boot Sector",
            new Color(0.55f, 0.7f, 0.85f, 1f),
            new Color(0.35f, 0.55f, 0.85f, 1f),
            new Color(0.95f, 0.55f, 0.25f, 1f)
    ),
    MIRROR(
            "Mirror District",
            new Color(0.75f, 0.45f, 0.95f, 1f),
            new Color(0.55f, 0.25f, 0.75f, 1f),
            new Color(0.95f, 0.35f, 0.85f, 1f)
    ),
    ROOT(
            "Root Spire",
            new Color(0.55f, 0.25f, 0.3f, 1f),
            new Color(0.75f, 0.2f, 0.25f, 1f),
            new Color(1f, 0.35f, 0.3f, 1f)
    ),
    ENDLESS(
            "Endless Protocol",
            new Color(0.25f, 0.65f, 0.7f, 1f),
            new Color(0.15f, 0.45f, 0.55f, 1f),
            new Color(0.4f, 1f, 0.85f, 1f)
    );

    public final String label;
    public final Color floorTint;
    public final Color wallTint;
    public final Color accentTint;

    BiomePalette(String label, Color floorTint, Color wallTint, Color accentTint) {
        this.label = label;
        this.floorTint = floorTint;
        this.wallTint = wallTint;
        this.accentTint = accentTint;
    }

    public static BiomePalette forChapter(String chapterId) {
        if (chapterId == null) {
            return ENDLESS;
        }
        return switch (chapterId) {
            case "mirror" -> MIRROR;
            case "root" -> ROOT;
            case "boot" -> BOOT;
            default -> ENDLESS;
        };
    }
}
