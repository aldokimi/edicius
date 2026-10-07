package com.edicius.run;

import com.edicius.sim.CombatBalance;
import com.edicius.sim.WeaponKit;

public class PlayerLoadout {

    public int maxHp = CombatBalance.PLAYER_MAX_HP;
    public int hp = CombatBalance.PLAYER_MAX_HP;
    public float fireRateMul = 1f;
    public float damageMul = 1f;
    public float moveMul = 1f;
    public float dashMul = 1f;
    public WeaponKit primary = WeaponKit.BOLT;
    public WeaponKit secondary = WeaponKit.SCATTER;
    public WeaponKit active = WeaponKit.BOLT;
    public boolean bonusWeaponUnlock;
    public boolean beamUnlocked;
    public int kills;
    public int roomsCleared;
    public float styleScore;

    public PlayerLoadout copy() {
        PlayerLoadout c = new PlayerLoadout();
        c.maxHp = maxHp;
        c.hp = hp;
        c.fireRateMul = fireRateMul;
        c.damageMul = damageMul;
        c.moveMul = moveMul;
        c.dashMul = dashMul;
        c.primary = primary;
        c.secondary = secondary;
        c.active = active;
        c.bonusWeaponUnlock = bonusWeaponUnlock;
        c.beamUnlocked = beamUnlocked;
        c.kills = kills;
        c.roomsCleared = roomsCleared;
        c.styleScore = styleScore;
        return c;
    }

    public void syncFromPlayer(com.edicius.sim.Player player) {
        hp = player.hp;
        maxHp = player.maxHp;
        kills = player.kills;
        active = player.weapon;
        styleScore = player.styleScore;
    }

    public void applyToPlayer(com.edicius.sim.Player player) {
        player.maxHp = maxHp;
        player.hp = hp;
        player.kills = kills;
        player.weapon = active;
        player.fireRateMul = fireRateMul;
        player.damageMul = damageMul;
        player.moveMul = moveMul;
        player.dashMul = dashMul;
        player.styleScore = styleScore;
        player.beamUnlocked = beamUnlocked;
        player.secondary = secondary;
    }
}
