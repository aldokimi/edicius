package com.edicius.sim;

import com.badlogic.gdx.Gdx;
import com.edicius.input.InputCommand;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class World {

    public static final int PLAYER_SHOT_DAMAGE = 20;
    public static final int ENEMY_SHOT_DAMAGE = 10;

    private final TileMap map;
    private final GridCollision collision;
    private final Player player;
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Projectile> projectiles = new ArrayList<>();
    private final Random random = new Random(42);

    private boolean paused;
    private boolean cleared;
    private boolean playerDead;

    public World(TileMap map) {
        this.map = map;
        this.collision = new GridCollision(map);
        this.player = new Player(map.tileCenterX(2), map.tileCenterY(2));
        spawnEnemies(3, 2);
    }

    public static World loadDefault() {
        return new World(TileMap.load(Gdx.files.internal("maps/map1.txt")));
    }

    private void spawnEnemies(int grunts, int shooters) {
        List<int[]> open = map.openTiles();
        for (int i = 0; i < grunts; i++) {
            int[] tile = open.get(random.nextInt(open.size()));
            enemies.add(new Enemy(EnemyRole.GRUNT, map.tileCenterX(tile[0]), map.tileCenterY(tile[1])));
        }
        for (int i = 0; i < shooters; i++) {
            int[] tile = open.get(random.nextInt(open.size()));
            enemies.add(new Enemy(EnemyRole.SHOOTER, map.tileCenterX(tile[0]), map.tileCenterY(tile[1])));
        }
    }

    public void spawnPlayerShot(float x, float y, float vx, float vy) {
        projectiles.add(new Projectile(x, y, vx, vy, Projectile.Owner.PLAYER, PLAYER_SHOT_DAMAGE));
    }

    public void spawnEnemyShot(float x, float y, float vx, float vy) {
        projectiles.add(new Projectile(x, y, vx, vy, Projectile.Owner.ENEMY, ENEMY_SHOT_DAMAGE));
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

        resolveHits();
        removeDead();

        if (!player.isAlive()) {
            playerDead = true;
        } else if (enemies.isEmpty()) {
            cleared = true;
        }
    }

    private void resolveHits() {
        for (Projectile shot : projectiles) {
            if (shot.dead) {
                continue;
            }
            if (shot.owner == Projectile.Owner.PLAYER) {
                for (Enemy enemy : enemies) {
                    if (!enemy.dead && overlaps(shot.x, shot.y, Projectile.HALF, enemy.x, enemy.y, Enemy.HALF_W, Enemy.HALF_H)) {
                        enemy.damage(shot.damage);
                        shot.dead = true;
                        if (enemy.dead) {
                            player.kills++;
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
        Iterator<Projectile> it = projectiles.iterator();
        while (it.hasNext()) {
            if (it.next().dead) {
                it.remove();
            }
        }
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

    public boolean isPaused() {
        return paused;
    }

    public boolean isCleared() {
        return cleared;
    }

    public boolean isPlayerDead() {
        return playerDead;
    }

    public GridCollision getCollision() {
        return collision;
    }
}
