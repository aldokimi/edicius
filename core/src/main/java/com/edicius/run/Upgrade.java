package com.edicius.run;

public enum Upgrade {
    OVERCLOCK("+20% fire rate", s -> s.fireRateMul *= 1.2f),
    HARDENED("+25 max HP and heal 25", s -> {
        s.maxHp += 25;
        s.hp = Math.min(s.maxHp, s.hp + 25);
    }),
    MAG_DUMP("+25% damage", s -> s.damageMul *= 1.25f),
    SLIPSTREAM("+15% move / dash", s -> {
        s.moveMul *= 1.15f;
        s.dashMul *= 1.15f;
    }),
    NANITES("Heal 40", s -> s.hp = Math.min(s.maxHp, s.hp + 40)),
    SECOND_CORE("Unlock next weapon slot kit", s -> s.bonusWeaponUnlock = true);

    public final String description;
    public final Applier applier;

    Upgrade(String description, Applier applier) {
        this.description = description;
        this.applier = applier;
    }

    @FunctionalInterface
    public interface Applier {
        void apply(PlayerLoadout state);
    }
}
