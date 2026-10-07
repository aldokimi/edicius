package com.edicius.sim;

import com.edicius.input.InputCommand;

public class Player {

    public static final float HALF_W = 12f;
    public static final float HALF_H = 18f;
    public static final float SPEED = 240f;
    public static final float DASH_DISTANCE = 220f;
    public static final float DASH_DURATION = 0.12f;
    public static final float FIRE_COOLDOWN = 0.2f;
    public static final float SHOT_SPEED = 600f;
    public static final int MAX_HP = 100;

    public float x;
    public float y;
    public int hp = MAX_HP;
    public int kills;

    private float dashTimeLeft;
    private float dashVx;
    private float dashVy;
    private float fireCooldown;

    public Player(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public boolean isInvulnerable() {
        return dashTimeLeft > 0f;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public void damage(int amount) {
        if (isInvulnerable() || amount <= 0) {
            return;
        }
        hp = Math.max(0, hp - amount);
    }

    public void update(InputCommand cmd, GridCollision collision, float dt, World world) {
        fireCooldown = Math.max(0f, fireCooldown - dt);

        if (cmd.dash() && dashTimeLeft <= 0f) {
            float dx = cmd.moveX();
            float dy = cmd.moveY();
            if (dx == 0f && dy == 0f) {
                float adx = cmd.aimWorldX() - x;
                float ady = cmd.aimWorldY() - y;
                float len = (float) Math.sqrt(adx * adx + ady * ady);
                if (len > 0.001f) {
                    dx = adx / len;
                    dy = ady / len;
                } else {
                    dx = 1f;
                }
            }
            float speed = DASH_DISTANCE / DASH_DURATION;
            dashVx = dx * speed;
            dashVy = dy * speed;
            dashTimeLeft = DASH_DURATION;
        }

        float vx;
        float vy;
        if (dashTimeLeft > 0f) {
            vx = dashVx;
            vy = dashVy;
            dashTimeLeft -= dt;
        } else {
            vx = cmd.moveX() * SPEED;
            vy = cmd.moveY() * SPEED;
        }

        tryMove(collision, vx * dt, 0f);
        tryMove(collision, 0f, vy * dt);

        if (cmd.fire() && fireCooldown <= 0f) {
            float adx = cmd.aimWorldX() - x;
            float ady = cmd.aimWorldY() - y;
            float len = (float) Math.sqrt(adx * adx + ady * ady);
            if (len < 0.001f) {
                adx = 1f;
                ady = 0f;
                len = 1f;
            }
            float sx = adx / len * SHOT_SPEED;
            float sy = ady / len * SHOT_SPEED;
            world.spawnPlayerShot(x, y, sx, sy);
            fireCooldown = FIRE_COOLDOWN;
        }
    }

    private void tryMove(GridCollision collision, float dx, float dy) {
        float nx = x + dx;
        float ny = y + dy;
        if (!collision.blocks(nx, ny, HALF_W, HALF_H)) {
            x = nx;
            y = ny;
        }
    }
}
