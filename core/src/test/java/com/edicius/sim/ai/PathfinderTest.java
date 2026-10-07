package com.edicius.sim.ai;

import com.edicius.sim.TileMap;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PathfinderTest {

    @Test
    void findsPathAroundWallBlock() {
        int[][] data = new int[8][8];
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                data[x][y] = 0;
            }
        }
        for (int y = 0; y < 6; y++) {
            data[3][y] = 1;
        }
        TileMap map = new TileMap(data);
        List<int[]> path = Pathfinder.findPath(map, 1, 1, 5, 1);
        assertFalse(path.isEmpty());
        assertTrue(path.size() > 2);
        assertTrue(path.get(0)[0] == 1 && path.get(0)[1] == 1);
        assertTrue(path.get(path.size() - 1)[0] == 5 && path.get(path.size() - 1)[1] == 1);
    }
}
