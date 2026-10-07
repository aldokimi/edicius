package com.edicius.sim;

public enum EnemyRole {
    GRUNT(28, 0f, 155f, 10, false, false),
    SHOOTER(34, 1.25f, 118f, 7, false, false),
    SHIELDER(52, 0f, 98f, 9, true, false),
    DRONE(22, 0.9f, 195f, 5, false, false),
    PATCHER(40, 0f, 95f, 7, false, true),
    HUNT_TARGET(58, 1.05f, 150f, 12, false, false),
    WARDEN(380, 1.4f, 88f, 14, false, false),
    ECHO(360, 1.0f, 118f, 11, false, false),
    GRID_ROOT(560, 1.15f, 78f, 13, false, false);

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
