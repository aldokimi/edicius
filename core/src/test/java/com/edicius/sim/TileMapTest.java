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

    @Test
    void fromRowsIsRowMajorNotTransposed() {
        // First data row is Y=0; value at file column 2 should land at tiles[2][0].
        int[][] rows = new int[20][20];
        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 20; x++) {
                rows[y][x] = 0;
            }
        }
        rows[0][2] = 1;
        rows[5][7] = 1;
        TileMap map = TileMap.fromRows(rows);
        assertTrue(map.isSolid(2, 0));
        assertTrue(map.isSolid(7, 5));
        assertFalse(map.isSolid(0, 2));
        assertFalse(map.isSolid(5, 7));
    }

    @Test
    void flushAgainstWallIsNotBlocked() {
        int[][] data = new int[20][20];
        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 20; y++) {
                data[x][y] = (x == 0 || y == 0 || x == 19 || y == 19) ? 1 : 0;
            }
        }
        GridCollision collision = new GridCollision(new TileMap(data));
        // Standing just inside open tile (1,1), right edge flush with wall at x=2*64.
        float x = TileMap.TILE_SIZE + 12f;
        float y = TileMap.TILE_SIZE + 32f;
        assertFalse(collision.blocks(x, y, 12f, 12f));
    }
}
