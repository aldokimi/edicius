package com.edicius.sim;

public class Projectile {

    public enum Owner {
        PLAYER,
        ENEMY
    }

    public static final float HALF = 4f;
    private static final float MAX_STEP = TileMap.TILE_SIZE * 0.45f;

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
        if (collision.blocks(x, y, HALF, HALF)) {
            dead = true;
            return;
        }
        float moveX = vx * dt;
        float moveY = vy * dt;
        float dist = (float) Math.sqrt(moveX * moveX + moveY * moveY);
        int steps = Math.max(1, (int) Math.ceil(dist / MAX_STEP));
        float inv = 1f / steps;
        for (int i = 0; i < steps; i++) {
            x += moveX * inv;
            y += moveY * inv;
            if (collision.blocks(x, y, HALF, HALF)) {
                dead = true;
                return;
            }
        }
    }
}
