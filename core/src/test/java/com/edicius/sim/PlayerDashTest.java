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
        World world = World.forTests(map);
        Player player = world.getPlayer();

        InputCommand dash = new InputCommand(1f, 0f, player.x + 100, player.y, false, true, false, false, false, false, false);
        player.update(dash, collision, SimClock.TICK, world);
        assertTrue(player.isInvulnerable());

        float t = 0f;
        while (t < Player.DASH_DURATION) {
            player.update(InputCommand.idle(player.x, player.y), collision, SimClock.TICK, world);
            t += SimClock.TICK;
        }
        assertFalse(player.isInvulnerable());
    }
}
