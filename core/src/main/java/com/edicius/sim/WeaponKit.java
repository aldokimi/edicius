package com.edicius.sim;

public enum WeaponKit {
    BOLT("Bolt Protocol", 0.18f, 620f, 22, 1, 0f),
    SCATTER("Scatter Protocol", 0.35f, 520f, 12, 3, 14f),
    BEAM("Beam Protocol", 0.08f, 900f, 8, 1, 0f);

    public final String displayName;
    public final float cooldown;
    public final float shotSpeed;
    public final int damage;
    public final int pellets;
    public final float spreadDegrees;

    WeaponKit(String displayName, float cooldown, float shotSpeed, int damage, int pellets, float spreadDegrees) {
        this.displayName = displayName;
        this.cooldown = cooldown;
        this.shotSpeed = shotSpeed;
        this.damage = damage;
        this.pellets = pellets;
        this.spreadDegrees = spreadDegrees;
    }
}
