package com.edicius.sim;

import com.edicius.input.InputCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerDashTest {

    @Test
    void dashGrantsIFramesForDashDuration() {
        int[][] open = new int[20][20];
        TileMap map = new TileMap(open);
        GridCollision collision = new GridCollision(map);
        World world = new World(map);
        Player player = world.getPlayer();

        InputCommand dash = new InputCommand(1f, 0f, player.x + 100, player.y, false, true, false);
        player.update(dash, collision, SimClock.TICK, world);
        assertTrue(player.isInvulnerable());

        float t = 0f;
        while (t < Player.DASH_DURATION) {
            player.update(new InputCommand(0, 0, player.x, player.y, false, false, false),
                    collision, SimClock.TICK, world);
            t += SimClock.TICK;
        }
        assertFalse(player.isInvulnerable());
    }
}
