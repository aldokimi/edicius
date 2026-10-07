package com.edicius.meta;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.edicius.gfx.ChromaPreset;
import com.edicius.run.PlayerLoadout;
import com.edicius.sim.CombatBalance;
import com.edicius.sim.WeaponKit;

import java.util.HashSet;
import java.util.Set;

public class MetaProgress {

    private static final String PREFS = "edicius_meta";

    public int protocolPoints;
    public int mobility;
    public int offense;
    public int defense;
    public int utility;
    public boolean act1Clear;
    public boolean act2Clear;
    public boolean act3Clear;
    public boolean endlessUnlocked;
    public boolean beamUnlocked;
    public boolean tutorialSeen;
    public String cosmeticTrail = "cyan";
    public String selectedCharacterId = "cipher";
    public ChromaPreset chroma = ChromaPreset.CYAN;
    public final Set<String> codex = new HashSet<>();
    public final Leaderboard leaderboard = new Leaderboard();
    public final DeathLog deathLog = new DeathLog();

    public static MetaProgress load() {
        MetaProgress m = new MetaProgress();
        Preferences p = Gdx.app.getPreferences(PREFS);
        m.protocolPoints = p.getInteger("points", 0);
        m.mobility = p.getInteger("mobility", 0);
        m.offense = p.getInteger("offense", 0);
        m.defense = p.getInteger("defense", 0);
        m.utility = p.getInteger("utility", 0);
        m.act1Clear = p.getBoolean("act1", false);
        m.act2Clear = p.getBoolean("act2", false);
        m.act3Clear = p.getBoolean("act3", false);
        m.endlessUnlocked = p.getBoolean("endless", false) || m.act3Clear;
        m.beamUnlocked = p.getBoolean("beam", false) || m.act2Clear;
        m.tutorialSeen = p.getBoolean("tutorial", false);
        m.cosmeticTrail = p.getString("trail", "cyan");
        m.selectedCharacterId = p.getString("character", "cipher");
        m.chroma = ChromaPreset.fromId(p.getString("chroma", ChromaPreset.CYAN.name()));
        String codexRaw = p.getString("codex", "");
        if (!codexRaw.isBlank()) {
            for (String id : codexRaw.split(",")) {
                if (!id.isBlank()) {
                    m.codex.add(id);
                }
            }
        }
        m.leaderboard.load(p);
        m.deathLog.load(p);
        return m;
    }

    public void save() {
        Preferences p = Gdx.app.getPreferences(PREFS);
        p.putInteger("points", protocolPoints);
        p.putInteger("mobility", mobility);
        p.putInteger("offense", offense);
        p.putInteger("defense", defense);
        p.putInteger("utility", utility);
        p.putBoolean("act1", act1Clear);
        p.putBoolean("act2", act2Clear);
        p.putBoolean("act3", act3Clear);
        p.putBoolean("endless", endlessUnlocked);
        p.putBoolean("beam", beamUnlocked);
        p.putBoolean("tutorial", tutorialSeen);
        p.putString("trail", cosmeticTrail);
        p.putString("character", selectedCharacterId);
        p.putString("chroma", chroma.name());
        p.putString("codex", String.join(",", codex));
        leaderboard.save(p);
        deathLog.save(p);
        p.flush();
    }

    public boolean chapterUnlocked(String id) {
        return switch (id) {
            case "boot" -> true;
            case "mirror" -> act1Clear;
            case "root" -> act2Clear;
            default -> false;
        };
    }

    public boolean spend(String branch) {
        if (protocolPoints <= 0) {
            return false;
        }
        switch (branch) {
            case "mobility" -> mobility++;
            case "offense" -> offense++;
            case "defense" -> defense++;
            case "utility" -> utility++;
            default -> {
                return false;
            }
        }
        protocolPoints--;
        save();
        return true;
    }

    public PlayerLoadout createLoadout() {
        PlayerLoadout l = new PlayerLoadout();
        l.maxHp = CombatBalance.PLAYER_MAX_HP + defense * 10;
        l.hp = l.maxHp;
        l.moveMul = 1f + mobility * 0.04f;
        l.dashMul = 1f + mobility * 0.04f;
        l.damageMul = 1f + offense * 0.05f;
        l.fireRateMul = 1f + utility * 0.03f;
        l.beamUnlocked = beamUnlocked;
        l.secondary = WeaponKit.SCATTER;
        l.active = WeaponKit.BOLT;
        return l;
    }

    public void awardRun(int kills, boolean victory, String chapterId, float style) {
        protocolPoints += Math.max(1, kills / 5 + (victory ? 5 : 1));
        unlockCodex("player");
        unlockCodex("nyx");
        if (victory) {
            if ("boot".equals(chapterId)) {
                act1Clear = true;
                unlockCodex("warden01");
            } else if ("mirror".equals(chapterId)) {
                act2Clear = true;
                beamUnlocked = true;
                unlockCodex("echo");
            } else if ("root".equals(chapterId)) {
                act3Clear = true;
                endlessUnlocked = true;
                unlockCodex("gridroot");
            }
        }
        if (style >= 55) {
            unlockCodex("style_a");
        }
        save();
    }

    public void unlockCodex(String id) {
        codex.add(id);
    }

    public void cycleCosmetic() {
        chroma = chroma.next();
        cosmeticTrail = chroma.name().toLowerCase();
        save();
    }

    public void selectCharacter(String id) {
        selectedCharacterId = id;
        save();
    }

    public void markTutorialSeen() {
        tutorialSeen = true;
        save();
    }
}
