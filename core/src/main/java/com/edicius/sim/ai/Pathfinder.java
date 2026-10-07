package com.edicius.sim.ai;

import com.edicius.sim.TileMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;

public final class Pathfinder {

    private Pathfinder() {
    }

    public static List<int[]> findPath(TileMap map, int startX, int startY, int goalX, int goalY) {
        if (map.isSolid(startX, startY) || map.isSolid(goalX, goalY)) {
            return List.of();
        }
        if (startX == goalX && startY == goalY) {
            return List.of(new int[]{startX, startY});
        }

        record Node(int x, int y, double g, double f, Node parent) {
        }

        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(n -> n.f));
        Map<Long, Double> bestG = new HashMap<>();
        Set<Long> closed = new HashSet<>();

        Node start = new Node(startX, startY, 0, heuristic(startX, startY, goalX, goalY), null);
        open.add(start);
        bestG.put(key(startX, startY), 0.0);

        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        while (!open.isEmpty()) {
            Node current = open.poll();
            long ck = key(current.x, current.y);
            if (closed.contains(ck)) {
                continue;
            }
            closed.add(ck);

            if (current.x == goalX && current.y == goalY) {
                List<int[]> path = new ArrayList<>();
                for (Node n = current; n != null; n = n.parent) {
                    path.add(new int[]{n.x, n.y});
                }
                Collections.reverse(path);
                return path;
            }

            for (int[] d : dirs) {
                int nx = current.x + d[0];
                int ny = current.y + d[1];
                if (map.isSolid(nx, ny)) {
                    continue;
                }
                if (d[0] != 0 && d[1] != 0) {
                    if (map.isSolid(current.x + d[0], current.y) || map.isSolid(current.x, current.y + d[1])) {
                        continue;
                    }
                }
                double step = (d[0] != 0 && d[1] != 0) ? 1.414 : 1.0;
                double g = current.g + step;
                long nk = key(nx, ny);
                if (g >= bestG.getOrDefault(nk, Double.POSITIVE_INFINITY)) {
                    continue;
                }
                bestG.put(nk, g);
                open.add(new Node(nx, ny, g, g + heuristic(nx, ny, goalX, goalY), current));
            }
        }
        return List.of();
    }

    private static double heuristic(int x, int y, int gx, int gy) {
        int dx = Math.abs(x - gx);
        int dy = Math.abs(y - gy);
        return Math.max(dx, dy) + (Math.sqrt(2) - 1) * Math.min(dx, dy);
    }

    private static long key(int x, int y) {
        return (((long) x) << 32) ^ (y & 0xffffffffL);
    }

    public static boolean sameCell(int[] a, int[] b) {
        return Objects.equals(a[0], b[0]) && Objects.equals(a[1], b[1]);
    }
}
