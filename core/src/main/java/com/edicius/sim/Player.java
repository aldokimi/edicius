package com.edicius.sim;

import com.edicius.gfx.Facing;
import com.edicius.input.InputCommand;

public class Player {

    public static final float HALF_W = 12f;
    public static final float HALF_H = 18f;
    public static final float BASE_SPEED = CombatBalance.PLAYER_SPEED;
    public static final float DASH_DISTANCE = CombatBalance.DASH_DISTANCE;
    public static final float DASH_DURATION = CombatBalance.DASH_DURATION;

    public float x;
    public float y;
    public int maxHp = CombatBalance.PLAYER_MAX_HP;
    public int hp = CombatBalance.PLAYER_MAX_HP;
    public int kills;
    public float styleScore;
    public float comboTimer;
    public int combo;
    public Facing facing = Facing.S;
    public boolean moving;
    /** Degrees for Kenney-style top-down sprites (0 = east). */
    public float aimAngleDeg;

    public WeaponKit weapon = WeaponKit.BOLT;
    public WeaponKit secondary = WeaponKit.SCATTER;
    public boolean beamUnlocked;
    public float fireRateMul = 1f;
    public float damageMul = 1f;
    public float moveMul = 1f;
    public float dashMul = 1f;

    public boolean eventDash;
    public boolean eventShoot;
    public boolean eventHurt;

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

    public void clearEvents() {
        eventDash = false;
        eventShoot = false;
        eventHurt = false;
    }

    public void damage(int amount) {
        if (isInvulnerable() || amount <= 0) {
            return;
        }
        hp = Math.max(0, hp - amount);
        combo = 0;
        comboTimer = 0f;
        eventHurt = true;
    }

    public void registerKill() {
        kills++;
        combo++;
        comboTimer = 2.5f;
        styleScore += 8 + combo * 2;
    }

    public void update(InputCommand cmd, GridCollision collision, float dt, World world) {
        clearEvents();
        fireCooldown = Math.max(0f, fireCooldown - dt);
        if (comboTimer > 0f) {
            comboTimer -= dt;
            if (comboTimer <= 0f) {
                combo = 0;
            }
        }

        if (cmd.weapon1()) {
            weapon = WeaponKit.BOLT;
        } else if (cmd.weapon2()) {
            weapon = secondary;
        } else if (cmd.weapon3() && beamUnlocked) {
            weapon = WeaponKit.BEAM;
        }

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
            float speed = (DASH_DISTANCE * dashMul) / DASH_DURATION;
            dashVx = dx * speed;
            dashVy = dy * speed;
            dashTimeLeft = DASH_DURATION;
            styleScore += 1.5f;
            eventDash = true;
            facing = Facing.fromVector(dx, dy);
        }

        float vx;
        float vy;
        if (dashTimeLeft > 0f) {
            vx = dashVx;
            vy = dashVy;
            dashTimeLeft -= dt;
            moving = true;
        } else {
            float speed = BASE_SPEED * moveMul;
            vx = cmd.moveX() * speed;
            vy = cmd.moveY() * speed;
            moving = vx != 0f || vy != 0f;
            if (moving) {
                facing = Facing.fromVector(cmd.moveX(), cmd.moveY());
            } else {
                facing = Facing.fromVector(cmd.aimWorldX() - x, cmd.aimWorldY() - y);
            }
        }

        tryMove(collision, vx * dt, 0f);
        tryMove(collision, 0f, vy * dt);

        float adx = cmd.aimWorldX() - x;
        float ady = cmd.aimWorldY() - y;
        if (adx * adx + ady * ady > 0.001f) {
            aimAngleDeg = (float) Math.toDegrees(Math.atan2(ady, adx));
        }

        if (cmd.fire() && fireCooldown <= 0f) {
            fire(cmd.aimWorldX(), cmd.aimWorldY(), world);
            fireCooldown = weapon.cooldown / Math.max(0.2f, fireRateMul);
            eventShoot = true;
        }
    }

    private void fire(float aimX, float aimY, World world) {
        float adx = aimX - x;
        float ady = aimY - y;
        float len = (float) Math.sqrt(adx * adx + ady * ady);
        if (len < 0.001f) {
            adx = 1f;
            ady = 0f;
            len = 1f;
        }
        float dirX = adx / len;
        float dirY = ady / len;
        facing = Facing.fromVector(dirX, dirY);
        int damage = Math.round(weapon.damage * damageMul * world.getPlayerDamageMul());
        float speed = weapon.shotSpeed * world.getProjectileSpeedMul();

        if (weapon.pellets <= 1) {
            world.spawnPlayerShot(x, y, dirX * speed, dirY * speed, damage);
            return;
        }
        float base = (float) Math.atan2(dirY, dirX);
        float spread = (float) Math.toRadians(weapon.spreadDegrees);
        for (int i = 0; i < weapon.pellets; i++) {
            float t = weapon.pellets == 1 ? 0f : (i / (float) (weapon.pellets - 1)) - 0.5f;
            float a = base + t * spread;
            world.spawnPlayerShot(x, y, (float) Math.cos(a) * speed, (float) Math.sin(a) * speed, damage);
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
