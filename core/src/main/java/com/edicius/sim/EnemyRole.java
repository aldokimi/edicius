package com.edicius.sim;

public enum EnemyRole {
    GRUNT(28, 0f, 150f, 10, false, false),
    SHOOTER(36, 1.15f, 120f, 7, false, false),
    SHIELDER(50, 0f, 100f, 9, true, false),
    DRONE(22, 0.85f, 190f, 5, false, false),
    PATCHER(40, 0f, 95f, 7, false, true),
    HUNT_TARGET(60, 1.0f, 145f, 12, false, false),
    WARDEN(360, 1.35f, 85f, 14, false, false),
    ECHO(340, 0.95f, 115f, 11, false, false),
    GRID_ROOT(520, 1.1f, 75f, 13, false, false);

    public final int maxHp;
    public final float shootInterval;
    public final float moveSpeed;
    public final int contactDamage;
    public final boolean shielded;
    public final boolean patcher;

    EnemyRole(int maxHp, float shootInterval, float moveSpeed, int contactDamage, boolean shielded, boolean patcher) {
        this.maxHp = maxHp;
        this.shootInterval = shootInterval;
        this.moveSpeed = moveSpeed;
        this.contactDamage = contactDamage;
        this.shielded = shielded;
        this.patcher = patcher;
    }
}
