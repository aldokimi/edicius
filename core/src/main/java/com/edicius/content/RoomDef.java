package com.edicius.content;

import com.edicius.run.Mutator;
import com.edicius.sim.ArenaType;

public class RoomDef {

    public final String id;
    public final String mapFile;
    public final ArenaType type;
    public final int grunts;
    public final int shooters;
    public final int shielders;
    public final int drones;
    public final int patchers;
    public final float holdSeconds;
    public final String bossId;
    public final Mutator mutator;
    public final String title;

    public RoomDef(
            String id,
            String title,
            String mapFile,
            ArenaType type,
            int grunts,
            int shooters,
            int shielders,
            int drones,
            int patchers,
            float holdSeconds,
            String bossId,
            Mutator mutator
    ) {
        this.id = id;
        this.title = title;
        this.mapFile = mapFile;
        this.type = type;
        this.grunts = grunts;
        this.shooters = shooters;
        this.shielders = shielders;
        this.drones = drones;
        this.patchers = patchers;
        this.holdSeconds = holdSeconds;
        this.bossId = bossId;
        this.mutator = mutator;
    }
}
