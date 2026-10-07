package com.edicius.sim;

import com.edicius.sim.ai.Pathfinder;

import java.util.List;

public class Enemy {

    public static final float HALF_W = 12f;
    public static final float HALF_H = 18f;
    public static final float SHOOT_RANGE = 400f;
    public static final float ENEMY_SHOT_SPEED = 380f;

    public final EnemyRole role;
    public float x;
    public float y;
    public int hp;
    public boolean dead;

    private float shootCooldown;
    private float contactCooldown;
    private List<int[]> path = List.of();
    private float repathTimer;

    public Enemy(EnemyRole role, float x, float y) {
        this.role = role;
        this.x = x;
        this.y = y;
        this.hp = role.maxHp;
        this.shootCooldown = role.shootInterval * 0.5f;
    }

    public void damage(int amount) {
        hp -= amount;
        if (hp <= 0) {
            dead = true;
        }
    }

    public void update(World world, GridCollision collision, Player player, float dt) {
        if (dead) {
            return;
        }
        repathTimer -= dt;
        if (repathTimer <= 0f || path.isEmpty()) {
            int sx = (int) (x / TileMap.TILE_SIZE);
            int sy = (int) (y / TileMap.TILE_SIZE);
            int gx = (int) (player.x / TileMap.TILE_SIZE);
            int gy = (int) (player.y / TileMap.TILE_SIZE);
            path = Pathfinder.findPath(collision.getMap(), sx, sy, gx, gy);
            repathTimer = 0.25f;
        }

        if (path.size() > 1) {
            int[] next = path.get(1);
            float tx = collision.getMap().tileCenterX(next[0]);
            float ty = collision.getMap().tileCenterY(next[1]);
            float dx = tx - x;
            float dy = ty - y;
            float len = (float) Math.sqrt(dx * dx + dy * dy);
            if (len < 6f) {
                path = path.subList(1, path.size());
            } else if (len > 0.001f) {
                float vx = dx / len * role.moveSpeed;
                float vy = dy / len * role.moveSpeed;
                tryMove(collision, vx * dt, 0f);
                tryMove(collision, 0f, vy * dt);
            }
        }

        float pdx = player.x - x;
        float pdy = player.y - y;
        float dist = (float) Math.sqrt(pdx * pdx + pdy * pdy);

        contactCooldown = Math.max(0f, contactCooldown - dt);
        if (dist < 28f && contactCooldown <= 0f) {
            player.damage(role.contactDamage);
            contactCooldown = 0.45f;
        }

        if (role.shootInterval > 0f) {
            shootCooldown -= dt;
            if (shootCooldown <= 0f && dist <= SHOOT_RANGE && dist > 0.001f) {
                float sx = pdx / dist * ENEMY_SHOT_SPEED;
                float sy = pdy / dist * ENEMY_SHOT_SPEED;
                world.spawnEnemyShot(x, y, sx, sy);
                shootCooldown = role.shootInterval;
            }
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
