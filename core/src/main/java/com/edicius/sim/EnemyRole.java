package com.edicius.sim;

public enum EnemyRole {
    GRUNT(30, 0f, 160f, 12, false, false),
    SHOOTER(40, 1.0f, 130f, 8, false, false),
    SHIELDER(55, 0f, 110f, 10, true, false),
    DRONE(25, 0.7f, 200f, 6, false, false),
    PATCHER(45, 0f, 100f, 8, false, true),
    HUNT_TARGET(70, 0.9f, 150f, 14, false, false),
    WARDEN(420, 1.2f, 90f, 18, false, false),
    ECHO(380, 0.85f, 120f, 12, false, false),
    GRID_ROOT(600, 1.0f, 80f, 16, false, false);

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
