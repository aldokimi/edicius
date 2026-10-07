package com.edicius.sim;

import com.edicius.content.RoomDef;
import com.edicius.input.InputCommand;
import com.edicius.run.Mutator;
import com.edicius.run.PlayerLoadout;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpawnPointsTest {

    /** map4 layout: tile (2,2) is solid — the old hardcoded player spawn. */
    private static TileMap map4Like() {
        int[][] rows = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 1, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 1, 1, 0, 1},
                {1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1},
                {1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1},
                {1, 1, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 0, 1, 1, 1},
                {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                {1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 1, 0, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                {1, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 0, 1},
                {1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1},
                {1, 0, 1, 1, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 1, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1},
                {1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 1, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        };
        return TileMap.fromRows(rows);
    }

    @Test
    void fromRowsKeepsMap4Tile22Solid() {
        TileMap map = map4Like();
        assertTrue(map.isSolid(2, 2));
        assertFalse(map.isSolid(1, 1));
    }

    @Test
    void findNearNeverReturnsSolidPreferredTile() {
        TileMap map = map4Like();
        GridCollision collision = new GridCollision(map);
        Random random = new Random(42L);
        for (int i = 0; i < 40; i++) {
            float[] pos = SpawnPoints.findNear(
                    map, collision, 2, 2, Player.HALF_W, Player.HALF_H, random);
            assertFalse(collision.blocks(pos[0], pos[1], Player.HALF_W, Player.HALF_H),
                    "spawn inside wall at " + pos[0] + "," + pos[1]);
        }
    }

    @Test
    void worldPlayerSpawnsOutsideWallsOnMap4() {
        TileMap map = map4Like();
        RoomDef room = new RoomDef(
                "hunt", "Hunt", "maps/map4.txt", ArenaType.HUNT,
                1, 0, 0, 0, 0, 0, null, Mutator.NONE);
        World world = new World(map, room, new PlayerLoadout(), Mutator.NONE, 99L);
        Player player = world.getPlayer();
        assertFalse(world.getCollision().blocks(player.x, player.y, Player.HALF_W, Player.HALF_H));
        for (Enemy enemy : world.getEnemies()) {
            assertFalse(world.getCollision().blocks(enemy.x, enemy.y, enemy.halfW(), enemy.halfH()),
                    "enemy " + enemy.role + " in wall");
        }
    }

    @Test
    void spawnAddDuringUpdateDoesNotThrow() {
        int[][] open = new int[20][20];
        TileMap map = new TileMap(open);
        RoomDef room = new RoomDef(
                "boss", "Boss", "maps/map1.txt", ArenaType.BOSS,
                0, 0, 0, 0, 0, 0, "gridroot", Mutator.NONE);
        World world = new World(map, room, new PlayerLoadout(), Mutator.NONE, 1L);
        for (Enemy e : world.getEnemies()) {
            e.hp = (int) (e.role.maxHp * 0.2f);
        }
        for (int i = 0; i < 180; i++) {
            world.update(InputCommand.idle(world.getPlayer().x, world.getPlayer().y), SimClock.TICK);
        }
        assertTrue(world.getEnemies().size() >= 1 || world.isCleared());
    }
}
