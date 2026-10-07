package com.edicius.sim;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LineOfSightTest {

    @Test
    void openArenaHasClearSight() {
        TileMap map = openArena();
        assertTrue(LineOfSight.clear(map, 96, 96, 400, 400));
    }

    @Test
    void wallBlocksSight() {
        int[][] rows = emptyRows();
        // Vertical wall at x=10, with a gap at y=10
        for (int y = 1; y < 19; y++) {
            rows[y][10] = 1;
        }
        rows[10][10] = 0;
        TileMap map = TileMap.fromRows(rows);
        // Through solid wall
        assertFalse(LineOfSight.clear(map, 5 * 64 + 32, 5 * 64 + 32, 15 * 64 + 32, 5 * 64 + 32));
        // Through the gap
        assertTrue(LineOfSight.clear(map, 5 * 64 + 32, 10 * 64 + 32, 15 * 64 + 32, 10 * 64 + 32));
    }

    private static TileMap openArena() {
        return TileMap.fromRows(emptyRows());
    }

    private static int[][] emptyRows() {
        int[][] rows = new int[20][20];
        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 20; x++) {
                rows[y][x] = (x == 0 || y == 0 || x == 19 || y == 19) ? 1 : 0;
            }
        }
        return rows;
    }
}
