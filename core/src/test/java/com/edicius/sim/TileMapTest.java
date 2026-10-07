package com.edicius.sim;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TileMapTest {

    @Test
    void cornerIsSolidAndInnerOpen() {
        int[][] data = new int[20][20];
        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 20; y++) {
                data[x][y] = (x == 0 || y == 0 || x == 19 || y == 19) ? 1 : 0;
            }
        }
        TileMap map = new TileMap(data);
        GridCollision collision = new GridCollision(map);

        assertTrue(map.isSolid(0, 0));
        assertFalse(map.isSolid(1, 1));
        assertTrue(collision.blocks(32, 32, 10, 10));
        assertFalse(collision.blocks(96, 96, 10, 10));
    }
}
