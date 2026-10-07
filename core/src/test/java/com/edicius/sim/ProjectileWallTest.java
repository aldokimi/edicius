package com.edicius.sim;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectileWallTest {

    @Test
    void projectileDiesOnWall() {
        int[][] data = new int[20][20];
        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 20; y++) {
                data[x][y] = (x == 0 || y == 0 || x == 19 || y == 19) ? 1 : 0;
            }
        }
        GridCollision collision = new GridCollision(new TileMap(data));
        Projectile shot = new Projectile(96, 96, -600, 0, Projectile.Owner.PLAYER, 20);
        for (int i = 0; i < 120; i++) {
            shot.update(collision, SimClock.TICK);
            if (shot.dead) {
                break;
            }
        }
        assertTrue(shot.dead);
    }
}
