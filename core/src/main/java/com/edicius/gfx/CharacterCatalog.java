package com.edicius.gfx;

import java.util.List;

public final class CharacterCatalog {

    private CharacterCatalog() {
    }

    public static final List<CharacterShell> SHELLS = List.of(
            shell("cipher", "Cipher", "Quiet netrunner shell. Clean lines, colder blood."),
            shell("neonjack", "Neon Jack", "Street-face protocol. Loud coat, louder score."),
            shell("grit", "Grit", "Ex-sec conversion. Heavy boots, heavier grudges."),
            shell("null7", "Null-7", "Synth chassis. Emotion optional, aim not."),
            shell("proxy", "Proxy", "Borrowed identity. Smiles in someone else's skin."),
            shell("apex", "Apex", "Raid veteran shell. Survived Root once — maybe.")
    );

    private static CharacterShell shell(String id, String name, String blurb) {
        return new CharacterShell(
                id,
                name,
                blurb,
                "runtime/characters/" + id + "/stand.png",
                "runtime/characters/" + id + "/armed.png"
        );
    }

    public static CharacterShell byId(String id) {
        for (CharacterShell s : SHELLS) {
            if (s.id.equals(id)) {
                return s;
            }
        }
        return SHELLS.get(0);
    }
}
