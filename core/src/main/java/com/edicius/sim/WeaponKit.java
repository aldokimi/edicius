package com.edicius.sim;

public enum WeaponKit {
    BOLT("Bolt Protocol", 0.16f, 640f, 24, 1, 0f),
    SCATTER("Scatter Protocol", 0.32f, 540f, 13, 3, 14f),
    BEAM("Beam Protocol", 0.07f, 920f, 9, 1, 0f);

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
