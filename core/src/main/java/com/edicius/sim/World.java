package com.edicius.sim;

import com.badlogic.gdx.Gdx;
import com.edicius.content.RoomDef;
import com.edicius.input.InputCommand;
import com.edicius.run.Mutator;
import com.edicius.run.PlayerLoadout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class World {

    public static final int ENEMY_SHOT_DAMAGE = CombatBalance.ENEMY_SHOT_DAMAGE;
    public boolean eventRoomClear;
    public boolean eventHitConfirm;
    private boolean clearEventArmed;

    private final TileMap map;
    private final GridCollision collision;
    private final Player player;
    private final RoomDef room;
    private final Mutator mutator;
    private final Random random;
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Enemy> pendingSpawns = new ArrayList<>();
    private final List<Projectile> projectiles = new ArrayList<>();
    private final List<Telegraph> telegraphs = new ArrayList<>();
    private final List<VfxBurst> bursts = new ArrayList<>();

    private boolean paused;
    private boolean cleared;
    private boolean playerDead;
    private float holdTimer;
    private float hackProgress;
    private Enemy huntTarget;
    private float objectiveX;
    private float objectiveY;

    public World(TileMap map, RoomDef room, PlayerLoadout loadout, Mutator mutator, long seed) {
        this.map = map;
        this.collision = new GridCollision(map);
        this.room = room;
        this.mutator = mutator == null ? Mutator.NONE : mutator;
        this.random = new Random(seed);

        float[] playerPos = SpawnPoints.findNear(
                map, collision, 2, 2, Player.HALF_W, Player.HALF_H, random);
        this.player = new Player(playerPos[0], playerPos[1]);
        loadout.applyToPlayer(player);
        if (this.mutator == Mutator.GLASS_CANNON) {
            player.maxHp = Math.max(40, (int) (player.maxHp * 0.7f));
            player.hp = Math.min(player.hp, player.maxHp);
            player.damageMul *= 1.35f;
        }
        holdTimer = room.holdSeconds;

        float[] obj = SpawnPoints.findNear(
                map, collision, map.width() / 2, map.height() / 2, 8f, 8f, random);
        objectiveX = obj[0];
        objectiveY = obj[1];

        spawnFromRoom();
    }

    public static World forRoom(RoomDef room, PlayerLoadout loadout, Mutator runMutator, long seed) {
        Mutator mut = room.mutator != Mutator.NONE ? room.mutator : runMutator;
        TileMap map = TileMap.load(Gdx.files.internal(room.mapFile));
        return new World(map, room, loadout, mut, seed);
    }

    /** Test helper without Gdx files. */
    public static World forTests(TileMap map) {
        RoomDef room = new RoomDef("t", "t", "maps/map1.txt", ArenaType.CLEAR, 0, 0, 0, 0, 0, 0, null, Mutator.NONE);
        return new World(map, room, new PlayerLoadout(), Mutator.NONE, 1L);
    }

    private void spawnFromRoom() {
        if (room.bossId != null) {
            EnemyRole bossRole = switch (room.bossId) {
                case "echo" -> EnemyRole.ECHO;
                case "gridroot" -> EnemyRole.GRID_ROOT;
                default -> EnemyRole.WARDEN;
            };
            Enemy boss = spawnEnemyAtPreferred(bossRole, 10, 10);
            enemies.add(boss);
            return;
        }
        spawnRole(EnemyRole.GRUNT, room.grunts);
        spawnRole(EnemyRole.SHOOTER, room.shooters);
        spawnRole(EnemyRole.SHIELDER, room.shielders);
        spawnRole(EnemyRole.DRONE, room.drones);
        spawnRole(EnemyRole.PATCHER, room.patchers);
        if (room.type == ArenaType.HUNT) {
            huntTarget = spawnEnemyAtPreferred(EnemyRole.HUNT_TARGET, 15, 15);
            huntTarget.huntMarked = true;
            enemies.add(huntTarget);
        }
        if (room.type == ArenaType.GAUNTLET) {
            spawnRole(EnemyRole.GRUNT, 2);
        }
    }

    private Enemy spawnEnemyAtPreferred(EnemyRole role, int preferX, int preferY) {
        float halfW = Enemy.HALF_W * (role == EnemyRole.WARDEN || role == EnemyRole.ECHO || role == EnemyRole.GRID_ROOT ? 1.8f : 1f);
        float halfH = Enemy.HALF_H * (role == EnemyRole.WARDEN || role == EnemyRole.ECHO || role == EnemyRole.GRID_ROOT ? 1.8f : 1f);
        float[] pos = SpawnPoints.findNear(map, collision, preferX, preferY, halfW, halfH, random);
        return new Enemy(role, pos[0], pos[1]);
    }

    private void spawnRole(EnemyRole role, int count) {
        if (count <= 0) {
            return;
        }
        for (int i = 0; i < count; i++) {
            float[] pos = SpawnPoints.findRandom(map, collision, Enemy.HALF_W, Enemy.HALF_H, random);
            enemies.add(new Enemy(role, pos[0], pos[1]));
        }
    }

    public void spawnAdd(EnemyRole role, float x, float y) {
        if (enemies.size() + pendingSpawns.size() > 40) {
            return;
        }
        float[] pos = SpawnPoints.fits(collision, x, y, Enemy.HALF_W, Enemy.HALF_H)
                ? new float[]{x, y}
                : SpawnPoints.findNear(map, collision,
                (int) (x / TileMap.TILE_SIZE), (int) (y / TileMap.TILE_SIZE),
                Enemy.HALF_W, Enemy.HALF_H, random);
        pendingSpawns.add(new Enemy(role, pos[0], pos[1]));
    }

    public void spawnPlayerShot(float x, float y, float vx, float vy, int damage) {
        projectiles.add(new Projectile(x, y, vx, vy, Projectile.Owner.PLAYER, damage));
    }

    public void spawnEnemyShot(float x, float y, float vx, float vy) {
        projectiles.add(new Projectile(x, y, vx, vy, Projectile.Owner.ENEMY, ENEMY_SHOT_DAMAGE));
    }

    public void addTelegraph(Telegraph t) {
        telegraphs.add(t);
    }

    public void update(InputCommand cmd, float dt) {
        eventHitConfirm = false;
        if (cmd.pauseToggle()) {
            paused = !paused;
        }
        if (paused || playerDead || cleared) {
            return;
        }

        player.update(cmd, collision, dt, this);
        unstick(player.x, player.y, Player.HALF_W, Player.HALF_H, (nx, ny) -> {
            player.x = nx;
            player.y = ny;
        });

        // Snapshot size so mid-frame pending adds don't get double-updated this tick.
        int enemyCount = enemies.size();
        for (int i = 0; i < enemyCount; i++) {
            enemies.get(i).update(this, collision, player, dt);
        }
        flushPendingSpawns();

        for (Projectile shot : projectiles) {
            shot.update(collision, dt);
        }
        updateTelegraphs(dt);
        updateVfx(dt);
        resolveHits();
        removeDead();
        updateObjective(cmd, dt);

        if (!player.isAlive()) {
            playerDead = true;
        }
        if (cleared && !clearEventArmed) {
            eventRoomClear = true;
            clearEventArmed = true;
        } else {
            eventRoomClear = false;
        }
    }

    private void flushPendingSpawns() {
        if (!pendingSpawns.isEmpty()) {
            enemies.addAll(pendingSpawns);
            pendingSpawns.clear();
        }
    }

    private void unstick(float x, float y, float halfW, float halfH, FloatBiConsumer moveTo) {
        if (!collision.blocks(x, y, halfW, halfH)) {
            return;
        }
        float[] safe = SpawnPoints.findNear(
                map, collision,
                (int) (x / TileMap.TILE_SIZE),
                (int) (y / TileMap.TILE_SIZE),
                halfW, halfH, random);
        moveTo.accept(safe[0], safe[1]);
    }

    @FunctionalInterface
    private interface FloatBiConsumer {
        void accept(float x, float y);
    }

    private void updateObjective(InputCommand cmd, float dt) {
        switch (room.type) {
            case CLEAR, GAUNTLET -> {
                if (enemies.isEmpty()) {
                    cleared = true;
                }
            }
            case HOLD -> {
                holdTimer -= dt;
                if (holdTimer <= 0f) {
                    cleared = true;
                }
            }
            case HUNT -> {
                if (huntTarget == null || huntTarget.dead) {
                    cleared = true;
                }
            }
            case HACK -> {
                float dx = player.x - objectiveX;
                float dy = player.y - objectiveY;
                if (dx * dx + dy * dy < 55f * 55f) {
                    hackProgress += dt * (cmd.interact() ? 1.75f : 1f);
                }
                if (hackProgress >= 10f) {
                    cleared = true;
                }
            }
            case BOSS -> {
                boolean bossAlive = false;
                for (Enemy e : enemies) {
                    if (e.role == EnemyRole.WARDEN || e.role == EnemyRole.ECHO || e.role == EnemyRole.GRID_ROOT) {
                        bossAlive = true;
                        break;
                    }
                }
                if (!bossAlive) {
                    cleared = true;
                }
            }
        }
    }

    private void updateTelegraphs(float dt) {
        Iterator<Telegraph> it = telegraphs.iterator();
        while (it.hasNext()) {
            Telegraph t = it.next();
            t.update(dt);
            if (t.isReady()) {
                t.resolved = true;
                float dx = player.x - t.x;
                float dy = player.y - t.y;
                if (dx * dx + dy * dy <= t.radius * t.radius) {
                    player.damage(t.damage);
                }
                bursts.add(new VfxBurst(t.x, t.y, 0.35f, 1f, 0.3f, 0.2f));
                it.remove();
            }
        }
    }

    private void updateVfx(float dt) {
        Iterator<VfxBurst> it = bursts.iterator();
        while (it.hasNext()) {
            VfxBurst b = it.next();
            b.life -= dt;
            if (b.life <= 0f) {
                it.remove();
            }
        }
    }

    private void resolveHits() {
        for (Projectile shot : projectiles) {
            if (shot.dead) {
                continue;
            }
            if (shot.owner == Projectile.Owner.PLAYER) {
                for (Enemy enemy : enemies) {
                    if (!enemy.dead && overlaps(shot.x, shot.y, Projectile.HALF, enemy.x, enemy.y, enemy.halfW(), enemy.halfH())) {
                        enemy.damage(shot.damage);
                        shot.dead = true;
                        bursts.add(new VfxBurst(enemy.x, enemy.y, 0.2f, 1f, 0.85f, 0.2f));
                        eventHitConfirm = true;
                        if (enemy.dead) {
                            player.registerKill();
                            bursts.add(new VfxBurst(enemy.x, enemy.y, 0.45f, 0.3f, 1f, 0.9f));
                            if (enemy.role == EnemyRole.WARDEN || enemy.role == EnemyRole.ECHO
                                    || enemy.role == EnemyRole.GRID_ROOT) {
                                player.styleScore += 40;
                            }
                        }
                        break;
                    }
                }
            } else if (!player.isInvulnerable()
                    && overlaps(shot.x, shot.y, Projectile.HALF, player.x, player.y, Player.HALF_W, Player.HALF_H)) {
                player.damage(shot.damage);
                shot.dead = true;
            }
        }
    }

    private static boolean overlaps(float ax, float ay, float aHalf, float bx, float by, float bHalfW, float bHalfH) {
        return Math.abs(ax - bx) <= aHalf + bHalfW && Math.abs(ay - by) <= aHalf + bHalfH;
    }

    private void removeDead() {
        enemies.removeIf(e -> e.dead);
        projectiles.removeIf(p -> p.dead);
    }

    public float getEnemySpeedMul() {
        return mutator.enemySpeedMul;
    }

    public float getPlayerDamageMul() {
        return mutator.playerDamageMul;
    }

    public float getProjectileSpeedMul() {
        return mutator.projectileSpeedMul;
    }

    public TileMap getMap() {
        return map;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    public List<Telegraph> getTelegraphs() {
        return telegraphs;
    }

    public List<VfxBurst> getBursts() {
        return bursts;
    }

    public RoomDef getRoom() {
        return room;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isCleared() {
        return cleared;
    }

    public boolean isPlayerDead() {
        return playerDead;
    }

    public float getHoldTimer() {
        return holdTimer;
    }

    public float getHackProgress() {
        return hackProgress;
    }

    public float getObjectiveX() {
        return objectiveX;
    }

    public float getObjectiveY() {
        return objectiveY;
    }

    public Mutator getMutator() {
        return mutator;
    }

    public GridCollision getCollision() {
        return collision;
    }
}
