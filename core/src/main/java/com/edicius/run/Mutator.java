package com.edicius.run;

public enum Mutator {
    NONE("Stable sector", 1f, 1f, 1f),
    OVERCLOCKED_GRID("GRID Overclock — enemies faster", 1.25f, 1.1f, 1f),
    LOW_GRAVITY("Low-G bullets — shots slower, floatier", 1f, 1f, 0.7f),
    GLASS_CANNON("Glass protocol — +damage / -max HP on start", 1f, 1.35f, 1f);

    public final String label;
    public final float enemySpeedMul;
    public final float playerDamageMul;
    public final float projectileSpeedMul;

    Mutator(String label, float enemySpeedMul, float playerDamageMul, float projectileSpeedMul) {
        this.label = label;
        this.enemySpeedMul = enemySpeedMul;
        this.playerDamageMul = playerDamageMul;
        this.projectileSpeedMul = projectileSpeedMul;
    }
}
