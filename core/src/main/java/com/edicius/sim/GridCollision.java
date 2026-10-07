package com.edicius.sim;

public class GridCollision {

    private final TileMap map;

    public GridCollision(TileMap map) {
        this.map = map;
    }

    public boolean blocks(float worldX, float worldY, float halfW, float halfH) {
        // Exclusive max edges so sitting flush on a tile boundary does not sample the next solid.
        final float eps = 0.001f;
        float minX = worldX - halfW;
        float maxX = worldX + halfW - eps;
        float minY = worldY - halfH;
        float maxY = worldY + halfH - eps;

        int x0 = (int) Math.floor(minX / TileMap.TILE_SIZE);
        int x1 = (int) Math.floor(maxX / TileMap.TILE_SIZE);
        int y0 = (int) Math.floor(minY / TileMap.TILE_SIZE);
        int y1 = (int) Math.floor(maxY / TileMap.TILE_SIZE);

        for (int x = x0; x <= x1; x++) {
            for (int y = y0; y <= y1; y++) {
                if (map.isSolid(x, y)) {
                    return true;
                }
            }
        }
        return false;
    }

    public TileMap getMap() {
        return map;
    }
}
