package com.edicius.gfx;

public class CharacterShell {

    public final String id;
    public final String displayName;
    public final String blurb;
    public final String standPath;
    public final String armedPath;

    public CharacterShell(String id, String displayName, String blurb, String standPath, String armedPath) {
        this.id = id;
        this.displayName = displayName;
        this.blurb = blurb;
        this.standPath = standPath;
        this.armedPath = armedPath;
    }
}
