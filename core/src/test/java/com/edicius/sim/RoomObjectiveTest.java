package com.edicius.sim;

import com.edicius.content.RoomDef;
import com.edicius.input.InputCommand;
import com.edicius.run.Mutator;
import com.edicius.run.PlayerLoadout;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RoomObjectiveTest {

    @Test
    void clearRoomCompletesWhenNoEnemies() {
        int[][] open = new int[20][20];
        TileMap map = new TileMap(open);
        RoomDef room = new RoomDef("c", "Clear", "maps/map1.txt", ArenaType.CLEAR, 0, 0, 0, 0, 0, 0, null, Mutator.NONE);
        World world = new World(map, room, new PlayerLoadout(), Mutator.NONE, 7L);
        world.update(InputCommand.idle(100, 100), SimClock.TICK);
        assertTrue(world.isCleared());
    }

    @Test
    void holdRoomCompletesAfterTimer() {
        int[][] open = new int[20][20];
        TileMap map = new TileMap(open);
        RoomDef room = new RoomDef("h", "Hold", "maps/map1.txt", ArenaType.HOLD, 0, 0, 0, 0, 0, 0.05f, null, Mutator.NONE);
        World world = new World(map, room, new PlayerLoadout(), Mutator.NONE, 7L);
        for (int i = 0; i < 10; i++) {
            world.update(InputCommand.idle(100, 100), SimClock.TICK);
        }
        assertTrue(world.isCleared());
    }
}
