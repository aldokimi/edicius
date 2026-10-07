package com.edicius.sim;

/**
 * Grid line-of-sight: a ray is clear if it never enters a solid tile
 * (endpoints may sit in open tiles).
 */
public final class LineOfSight {

    private LineOfSight() {
    }

    public static boolean clear(TileMap map, float x0, float y0, float x1, float y1) {
        float dx = x1 - x0;
        float dy = y1 - y0;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist < 1f) {
            return true;
        }
        float step = TileMap.TILE_SIZE * 0.2f;
        int steps = Math.max(1, (int) Math.ceil(dist / step));
        float inv = 1f / steps;
        // Skip first sample (inside shooter) and last (inside target).
        for (int i = 1; i < steps; i++) {
            float t = i * inv;
            float x = x0 + dx * t;
            float y = y0 + dy * t;
            int tx = (int) Math.floor(x / TileMap.TILE_SIZE);
            int ty = (int) Math.floor(y / TileMap.TILE_SIZE);
            if (map.isSolid(tx, ty)) {
                return false;
            }
        }
        return true;
    }
}
