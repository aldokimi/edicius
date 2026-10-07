package com.edicius.content;

import com.edicius.run.Mutator;
import com.edicius.sim.ArenaType;

import java.util.ArrayList;
import java.util.List;

public final class ContentLibrary {

    private ContentLibrary() {
    }

    public static List<ChapterDef> chapters() {
        List<ChapterDef> list = new ArrayList<>();
        list.add(bootSector());
        list.add(mirrorDistrict());
        list.add(rootSpire());
        return list;
    }

    public static ChapterDef byId(String id) {
        for (ChapterDef c : chapters()) {
            if (c.id.equals(id)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Unknown chapter: " + id);
    }

    public static List<RoomDef> endlessPool() {
        List<RoomDef> pool = new ArrayList<>();
        for (ChapterDef c : chapters()) {
            for (RoomDef r : c.rooms) {
                if (r.type != ArenaType.BOSS) {
                    pool.add(r);
                }
            }
        }
        return pool;
    }

    private static ChapterDef bootSector() {
        return new ChapterDef(
                "boot",
                "Act 1 — Boot Sector",
                "Nyx: Boot Sector is loud and dumb. Clear the yards, don't die pretty. Warden-01 still thinks walls are a personality.",
                1,
                List.of(
                        room("b1", "Server Alley", "maps/map1.txt", ArenaType.CLEAR, 3, 1, 0, 0, 0, 0, null, Mutator.NONE),
                        room("b2", "Surge Bay", "maps/map2.txt", ArenaType.HOLD, 2, 2, 0, 0, 0, 28f, null, Mutator.NONE),
                        room("b3", "Mark the Runner", "maps/map1.txt", ArenaType.HUNT, 4, 1, 0, 0, 0, 0, null, Mutator.NONE),
                        room("b4", "Cable Gauntlet", "maps/map3.txt", ArenaType.GAUNTLET, 5, 2, 0, 0, 0, 0, null, Mutator.NONE),
                        room("b5", "Warden Lock", "maps/map2.txt", ArenaType.BOSS, 0, 0, 0, 0, 0, 0, "warden01", Mutator.NONE)
                ),
                "scatter_ready"
        );
    }

    private static ChapterDef mirrorDistrict() {
        return new ChapterDef(
                "mirror",
                "Act 2 — Mirror District",
                "Nyx: Reflections lie. Shielders front, drones flank. If my voice stutters—ignore the stutter. Probably.",
                2,
                List.of(
                        room("m1", "Neon Concourse", "maps/map2.txt", ArenaType.CLEAR, 3, 2, 1, 0, 0, 0, null, Mutator.NONE),
                        room("m2", "Billboard Hold", "maps/map3.txt", ArenaType.HOLD, 2, 2, 1, 1, 0, 32f, null, Mutator.OVERCLOCKED_GRID),
                        room("m3", "Glass Hunt", "maps/map1.txt", ArenaType.HUNT, 3, 2, 1, 1, 0, 0, null, Mutator.NONE),
                        room("m4", "Hack Node", "maps/map2.txt", ArenaType.HACK, 2, 2, 1, 1, 0, 20f, null, Mutator.LOW_GRAVITY),
                        room("m5", "Mirror Cut", "maps/map3.txt", ArenaType.GAUNTLET, 4, 2, 2, 1, 0, 0, null, Mutator.NONE),
                        room("m6", "Echo Broker", "maps/map1.txt", ArenaType.BOSS, 0, 0, 0, 0, 0, 0, "echo", Mutator.NONE)
                ),
                "beam_ready"
        );
    }

    private static ChapterDef rootSpire() {
        return new ChapterDef(
                "root",
                "Act 3 — Root Spire",
                "Nyx: Root Spire. Patchers buff the choir. End the song. If we win, other Edicii might hear the silence.",
                3,
                List.of(
                        room("r1", "Ring Approach", "maps/map3.txt", ArenaType.CLEAR, 3, 2, 1, 1, 1, 0, null, Mutator.NONE),
                        room("r2", "Collapse Hold", "maps/map2.txt", ArenaType.HOLD, 2, 2, 1, 1, 1, 35f, null, Mutator.GLASS_CANNON),
                        room("r3", "Priority Patch", "maps/map1.txt", ArenaType.HUNT, 3, 2, 1, 1, 2, 0, null, Mutator.OVERCLOCKED_GRID),
                        room("r4", "Spire Hack", "maps/map3.txt", ArenaType.HACK, 2, 3, 2, 1, 1, 22f, null, Mutator.NONE),
                        room("r5", "Final Corridor", "maps/map2.txt", ArenaType.GAUNTLET, 5, 3, 2, 2, 1, 0, null, Mutator.NONE),
                        room("r6", "GRID Root", "maps/map1.txt", ArenaType.BOSS, 0, 0, 0, 0, 0, 0, "gridroot", Mutator.NONE)
                ),
                "endless_ready"
        );
    }

    private static RoomDef room(
            String id, String title, String map, ArenaType type,
            int g, int s, int sh, int d, int p, float hold, String boss, Mutator mut
    ) {
        return new RoomDef(id, title, map, type, g, s, sh, d, p, hold, boss, mut);
    }
}
