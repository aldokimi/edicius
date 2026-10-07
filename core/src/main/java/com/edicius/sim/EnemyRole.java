package com.edicius.sim;

public enum EnemyRole {
    GRUNT(30, 0f, 160f, 12),
    SHOOTER(40, 1.0f, 130f, 8);

    public final int maxHp;
    public final float shootInterval;
    public final float moveSpeed;
    public final int contactDamage;

    EnemyRole(int maxHp, float shootInterval, float moveSpeed, int contactDamage) {
        this.maxHp = maxHp;
        this.shootInterval = shootInterval;
        this.moveSpeed = moveSpeed;
        this.contactDamage = contactDamage;
    }
}
