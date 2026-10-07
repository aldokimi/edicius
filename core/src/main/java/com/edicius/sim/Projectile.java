package com.edicius.sim;

public class Projectile {

    public enum Owner {
        PLAYER,
        ENEMY
    }

    public static final float HALF = 4f;

    public float x;
    public float y;
    public float vx;
    public float vy;
    public final Owner owner;
    public final int damage;
    public boolean dead;

    public Projectile(float x, float y, float vx, float vy, Owner owner, int damage) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.owner = owner;
        this.damage = damage;
    }

    public void update(GridCollision collision, float dt) {
        if (dead) {
            return;
        }
        x += vx * dt;
        y += vy * dt;
        if (collision.blocks(x, y, HALF, HALF)) {
            dead = true;
        }
    }
}
