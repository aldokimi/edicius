package com.edicius.sim;

import com.edicius.sim.ai.Pathfinder;

import java.util.ArrayList;
import java.util.List;

public class Enemy {

    public static final float HALF_W = 12f;
    public static final float HALF_H = 18f;
    public com.edicius.gfx.Facing facing = com.edicius.gfx.Facing.S;
    public boolean moving;
    public float aimAngleDeg;
    public static final float SHOOT_RANGE = 400f;
    public static final float ENEMY_SHOT_SPEED = 380f;

    public final EnemyRole role;
    public float x;
    public float y;
    public int hp;
    public boolean dead;
    public boolean huntMarked;
    public float scale = 1f;

    private float shootCooldown;
    private float contactCooldown;
    private float abilityCooldown;
    private float patchTimer;
    private List<int[]> path = List.of();
    private float repathTimer;
    private int phase = 1;

    public Enemy(EnemyRole role, float x, float y) {
        this.role = role;
        this.x = x;
        this.y = y;
        this.hp = role.maxHp;
        this.shootCooldown = role.shootInterval * 0.5f;
        if (role == EnemyRole.WARDEN || role == EnemyRole.ECHO || role == EnemyRole.GRID_ROOT) {
            scale = 1.8f;
            abilityCooldown = 1.5f;
        }
    }

    public float halfW() {
        return HALF_W * scale;
    }

    public float halfH() {
        return HALF_H * scale;
    }

    public void damage(int amount) {
        int dmg = amount;
        if (role.shielded) {
            dmg = Math.max(1, amount / 2);
        }
        hp -= dmg;
        if (hp <= 0) {
            dead = true;
        }
    }

    public void update(World world, GridCollision collision, Player player, float dt) {
        if (dead) {
            return;
        }

        float speedMul = world.getEnemySpeedMul();
        if (role.patcher) {
            patchTimer -= dt;
            if (patchTimer <= 0f) {
                buffNearby(world);
                patchTimer = 0.75f;
            }
        }

        repathTimer -= dt;
        if (repathTimer <= 0f || path.isEmpty()) {
            int sx = (int) (x / TileMap.TILE_SIZE);
            int sy = (int) (y / TileMap.TILE_SIZE);
            int gx = (int) (player.x / TileMap.TILE_SIZE);
            int gy = (int) (player.y / TileMap.TILE_SIZE);
            path = Pathfinder.findPath(collision.getMap(), sx, sy, gx, gy);
            repathTimer = role == EnemyRole.DRONE ? 0.15f : 0.25f;
        }

        if (path.size() > 1) {
            int[] next = path.get(1);
            float tx = collision.getMap().tileCenterX(next[0]);
            float ty = collision.getMap().tileCenterY(next[1]);
            float dx = tx - x;
            float dy = ty - y;
            float len = (float) Math.sqrt(dx * dx + dy * dy);
            if (len < 6f) {
                path = new ArrayList<>(path.subList(1, path.size()));
            } else if (len > 0.001f) {
                float move = role.moveSpeed * speedMul;
                float vx = dx / len * move;
                float vy = dy / len * move;
                facing = com.edicius.gfx.Facing.fromVector(dx, dy);
                moving = true;
                tryMove(collision, vx * dt, 0f);
                tryMove(collision, 0f, vy * dt);
            } else {
                moving = false;
            }
        } else {
            moving = false;
        }

        float pdx = player.x - x;
        float pdy = player.y - y;
        float dist = (float) Math.sqrt(pdx * pdx + pdy * pdy);
        if (dist > 0.001f) {
            aimAngleDeg = (float) Math.toDegrees(Math.atan2(pdy, pdx));
        }

        contactCooldown = Math.max(0f, contactCooldown - dt);
        if (dist < 28f * scale && contactCooldown <= 0f) {
            player.damage(role.contactDamage);
            contactCooldown = CombatBalance.CONTACT_COOLDOWN;
        }

        if (role.shootInterval > 0f) {
            shootCooldown -= dt;
            boolean canSee = dist <= SHOOT_RANGE && dist > 0.001f
                    && LineOfSight.clear(collision.getMap(), x, y, player.x, player.y);
            if (shootCooldown <= 0f && canSee) {
                float shotSpeed = ENEMY_SHOT_SPEED * world.getProjectileSpeedMul();
                if (role == EnemyRole.ECHO && phase >= 2) {
                    // mirror volley
                    for (int i = -1; i <= 1; i++) {
                        float a = (float) Math.atan2(pdy, pdx) + i * 0.25f;
                        world.spawnEnemyShot(x, y, (float) Math.cos(a) * shotSpeed, (float) Math.sin(a) * shotSpeed);
                    }
                } else {
                    world.spawnEnemyShot(x, y, pdx / dist * shotSpeed, pdy / dist * shotSpeed);
                }
                shootCooldown = role.shootInterval / speedMul;
            }
        }

        abilityCooldown -= dt;
        if (abilityCooldown <= 0f && LineOfSight.clear(collision.getMap(), x, y, player.x, player.y)) {
            useAbility(world, player);
        }

        if (role == EnemyRole.ECHO && hp < role.maxHp * 0.5f) {
            phase = 2;
        }
        if (role == EnemyRole.GRID_ROOT) {
            if (hp < role.maxHp * 0.33f) {
                phase = 3;
            } else if (hp < role.maxHp * 0.66f) {
                phase = 2;
            }
        }
    }

    private void useAbility(World world, Player player) {
        switch (role) {
            case WARDEN -> {
                world.addTelegraph(new Telegraph(player.x, player.y, 70f, CombatBalance.WARDEN_TELEGRAPH, 24));
                abilityCooldown = 2.6f;
            }
            case ECHO -> {
                world.addTelegraph(new Telegraph(x, y, 90f, 0.7f, 22));
                abilityCooldown = 2.0f;
            }
            case GRID_ROOT -> {
                world.addTelegraph(new Telegraph(player.x, player.y, 55f + phase * 10f, 0.65f, 18 + phase * 4));
                // spawn adds on later phases
                if (phase >= 2) {
                    world.spawnAdd(EnemyRole.DRONE, x + 40, y);
                }
                if (phase >= 3) {
                    world.spawnAdd(EnemyRole.PATCHER, x - 40, y);
                }
                abilityCooldown = 1.6f;
            }
            default -> abilityCooldown = 999f;
        }
    }

    private void buffNearby(World world) {
        for (Enemy other : world.getEnemies()) {
            if (other == this || other.dead) {
                continue;
            }
            float dx = other.x - x;
            float dy = other.y - y;
            if (dx * dx + dy * dy < 140f * 140f) {
                other.hp = Math.min(other.role.maxHp, other.hp + 1);
            }
        }
    }

    private void tryMove(GridCollision collision, float dx, float dy) {
        float nx = x + dx;
        float ny = y + dy;
        if (!collision.blocks(nx, ny, halfW(), halfH())) {
            x = nx;
            y = ny;
        }
    }
}
