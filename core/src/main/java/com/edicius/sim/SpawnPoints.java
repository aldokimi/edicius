package com.edicius.sim;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/** Finds walkable spawn positions that fit an entity collision box. */
public final class SpawnPoints {

    private SpawnPoints() {
    }

    public static boolean fits(GridCollision collision, float worldX, float worldY, float halfW, float halfH) {
        return !collision.blocks(worldX, worldY, halfW, halfH);
    }

    public static float[] findNear(
            TileMap map,
            GridCollision collision,
            int preferX,
            int preferY,
            float halfW,
            float halfH,
            Random random
    ) {
        List<int[]> open = walkableTiles(map, collision, halfW, halfH);
        if (open.isEmpty() && (halfW > 8f || halfH > 8f)) {
            open = walkableTiles(map, collision, Math.min(halfW, 8f), Math.min(halfH, 8f));
        }
        if (open.isEmpty()) {
            open = openTileCenters(map);
        }
        if (open.isEmpty()) {
            return new float[]{map.worldWidth() * 0.5f, map.worldHeight() * 0.5f};
        }
        open.sort(Comparator.comparingInt(t -> Math.abs(t[0] - preferX) + Math.abs(t[1] - preferY)));
        int window = Math.min(8, open.size());
        int[] tile = open.get(random.nextInt(window));
        return new float[]{map.tileCenterX(tile[0]), map.tileCenterY(tile[1])};
    }

    public static float[] findRandom(TileMap map, GridCollision collision, float halfW, float halfH, Random random) {
        List<int[]> open = walkableTiles(map, collision, halfW, halfH);
        if (open.isEmpty()) {
            open = openTileCenters(map);
        }
        if (open.isEmpty()) {
            return new float[]{map.worldWidth() * 0.5f, map.worldHeight() * 0.5f};
        }
        int[] tile = open.get(random.nextInt(open.size()));
        return new float[]{map.tileCenterX(tile[0]), map.tileCenterY(tile[1])};
    }

    public static List<int[]> walkableTiles(TileMap map, GridCollision collision, float halfW, float halfH) {
        List<int[]> open = new ArrayList<>();
        for (int x = 0; x < map.width(); x++) {
            for (int y = 0; y < map.height(); y++) {
                if (map.isSolid(x, y)) {
                    continue;
                }
                float cx = map.tileCenterX(x);
                float cy = map.tileCenterY(y);
                if (fits(collision, cx, cy, halfW, halfH)) {
                    open.add(new int[]{x, y});
                }
            }
        }
        return open;
    }

    private static List<int[]> openTileCenters(TileMap map) {
        List<int[]> open = new ArrayList<>();
        for (int x = 0; x < map.width(); x++) {
            for (int y = 0; y < map.height(); y++) {
                if (!map.isSolid(x, y)) {
                    open.add(new int[]{x, y});
                }
            }
        }
        return open;
    }
}
