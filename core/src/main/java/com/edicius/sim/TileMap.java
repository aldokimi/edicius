package com.edicius.sim;

import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class TileMap {

    public static final int TILE_SIZE = 64;

    private final int[][] tiles;

    public TileMap(int[][] tiles) {
        this.tiles = tiles;
    }

    public static TileMap load(FileHandle file) {
        try (Scanner scanner = new Scanner(file.read())) {
            scanner.useLocale(Locale.US);
            int[][] data = new int[20][20];
            for (int x = 0; x < 20; x++) {
                for (int y = 0; y < 20; y++) {
                    if (!scanner.hasNextInt()) {
                        throw new IllegalArgumentException("Map must be 20x20 ints: " + file.path());
                    }
                    data[x][y] = scanner.nextInt();
                }
            }
            return new TileMap(data);
        }
    }

    public int width() {
        return tiles.length;
    }

    public int height() {
        return tiles[0].length;
    }

    public boolean isSolid(int tileX, int tileY) {
        if (tileX < 0 || tileY < 0 || tileX >= width() || tileY >= height()) {
            return true;
        }
        return tiles[tileX][tileY] == 1;
    }

    public int[][] raw() {
        return tiles;
    }

    public float worldWidth() {
        return width() * TILE_SIZE;
    }

    public float worldHeight() {
        return height() * TILE_SIZE;
    }

    public List<int[]> openTiles() {
        List<int[]> open = new ArrayList<>();
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                if (!isSolid(x, y)) {
                    open.add(new int[]{x, y});
                }
            }
        }
        return open;
    }

    public float tileCenterX(int tileX) {
        return tileX * TILE_SIZE + TILE_SIZE * 0.5f;
    }

    public float tileCenterY(int tileY) {
        return tileY * TILE_SIZE + TILE_SIZE * 0.5f;
    }
}
