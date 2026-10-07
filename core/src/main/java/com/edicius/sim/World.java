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

    public static final int ENEMY_SHOT_DAMAGE = 10;

    private final TileMap map;
    private final GridCollision collision;
    private final Player player;
    private final RoomDef room;
    private final Mutator mutator;
    private final Random random;
    private final List<Enemy> enemies = new ArrayList<>();
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
        this.player = new Player(map.tileCenterX(2), map.tileCenterY(2));
        loadout.applyToPlayer(player);
        if (this.mutator == Mutator.GLASS_CANNON) {
            player.maxHp = Math.max(40, (int) (player.maxHp * 0.7f));
            player.hp = Math.min(player.hp, player.maxHp);
            player.damageMul *= 1.35f;
        }
        holdTimer = room.holdSeconds;
        objectiveX = map.worldWidth() * 0.5f;
        objectiveY = map.worldHeight() * 0.5f;
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
            enemies.add(new Enemy(bossRole, map.tileCenterX(10), map.tileCenterY(10)));
            return;
        }
        spawnRole(EnemyRole.GRUNT, room.grunts);
        spawnRole(EnemyRole.SHOOTER, room.shooters);
        spawnRole(EnemyRole.SHIELDER, room.shielders);
        spawnRole(EnemyRole.DRONE, room.drones);
        spawnRole(EnemyRole.PATCHER, room.patchers);
        if (room.type == ArenaType.HUNT) {
            huntTarget = new Enemy(EnemyRole.HUNT_TARGET, map.tileCenterX(15), map.tileCenterY(15));
            huntTarget.huntMarked = true;
            enemies.add(huntTarget);
        }
        if (room.type == ArenaType.GAUNTLET) {
            spawnRole(EnemyRole.GRUNT, 2);
        }
    }

    private void spawnRole(EnemyRole role, int count) {
        List<int[]> open = map.openTiles();
        for (int i = 0; i < count; i++) {
            int[] tile = open.get(random.nextInt(open.size()));
            enemies.add(new Enemy(role, map.tileCenterX(tile[0]), map.tileCenterY(tile[1])));
        }
    }

    public void spawnAdd(EnemyRole role, float x, float y) {
        if (enemies.size() > 40) {
            return;
        }
        enemies.add(new Enemy(role, x, y));
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
        if (cmd.pauseToggle()) {
            paused = !paused;
        }
        if (paused || playerDead || cleared) {
            return;
        }

        player.update(cmd, collision, dt, this);

        for (Enemy enemy : enemies) {
            enemy.update(this, collision, player, dt);
        }
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
