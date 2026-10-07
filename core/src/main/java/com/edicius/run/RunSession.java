package com.edicius.run;

import com.edicius.content.ChapterDef;
import com.edicius.content.ContentLibrary;
import com.edicius.content.RoomDef;
import com.edicius.sim.ArenaType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RunSession {

    public enum Mode {
        CAMPAIGN,
        DAILY,
        ENDLESS
    }

    public final Mode mode;
    public final ChapterDef chapter;
    public final long seed;
    public final PlayerLoadout loadout;
    public final Random random;
    public int roomIndex;
    public boolean victory;
    public boolean failed;
    public String lastNyxLine;
    public Mutator activeMutator = Mutator.NONE;
    public int endlessDepth;

    public RunSession(Mode mode, ChapterDef chapter, long seed, PlayerLoadout loadout) {
        this.mode = mode;
        this.chapter = chapter;
        this.seed = seed;
        this.loadout = loadout;
        this.random = new Random(seed);
        this.roomIndex = 0;
        this.lastNyxLine = chapter != null ? chapter.nyxBrief : "Nyx: Endless Protocol online. Don't get poetic.";
        if (mode == Mode.DAILY) {
            Mutator[] m = {Mutator.OVERCLOCKED_GRID, Mutator.LOW_GRAVITY, Mutator.GLASS_CANNON};
            activeMutator = m[random.nextInt(m.length)];
        }
    }

    public static RunSession campaign(ChapterDef chapter, PlayerLoadout base, long seed) {
        return new RunSession(Mode.CAMPAIGN, chapter, seed, base.copy());
    }

    public static RunSession daily(PlayerLoadout base, long seed) {
        return new RunSession(Mode.DAILY, ContentLibrary.byId("boot"), seed, base.copy());
    }

    public static RunSession endless(PlayerLoadout base, long seed) {
        return new RunSession(Mode.ENDLESS, null, seed, base.copy());
    }

    public RoomDef currentRoom() {
        if (mode == Mode.ENDLESS) {
            List<RoomDef> pool = ContentLibrary.endlessPool();
            RoomDef template = pool.get(Math.floorMod(endlessDepth, pool.size()));
            return scaleEndless(template, endlessDepth);
        }
        return chapter.rooms.get(roomIndex);
    }

    private RoomDef scaleEndless(RoomDef t, int depth) {
        int add = depth / 2;
        return new RoomDef(
                t.id + "_e" + depth,
                "Endless " + (depth + 1) + ": " + t.title,
                t.mapFile,
                t.type == ArenaType.BOSS ? ArenaType.CLEAR : t.type,
                t.grunts + add,
                t.shooters + add / 2,
                t.shielders + (depth > 3 ? 1 : 0),
                t.drones + (depth > 5 ? 1 : 0),
                t.patchers + (depth > 7 ? 1 : 0),
                t.holdSeconds > 0 ? t.holdSeconds + depth : t.holdSeconds,
                null,
                depth % 4 == 3 ? Mutator.OVERCLOCKED_GRID : t.mutator
        );
    }

    public boolean hasMoreRooms() {
        if (mode == Mode.ENDLESS) {
            return true;
        }
        return roomIndex < chapter.rooms.size() - 1;
    }

    public void advanceRoom() {
        loadout.roomsCleared++;
        if (mode == Mode.ENDLESS) {
            endlessDepth++;
            return;
        }
        roomIndex++;
    }

    public List<Upgrade> rollUpgrades() {
        List<Upgrade> all = new ArrayList<>(List.of(Upgrade.values()));
        Collections.shuffle(all, random);
        return new ArrayList<>(all.subList(0, Math.min(3, all.size())));
    }

    public String resultsSummary() {
        String rank = StyleRank.rank(loadout.styleScore);
        if (failed) {
            return "Raid failed — kills " + loadout.kills + " — style " + rank;
        }
        if (mode == Mode.ENDLESS) {
            return "Endless depth " + endlessDepth + " — kills " + loadout.kills + " — style " + rank;
        }
        return (victory ? "Sector secured" : "Extracted") + " — kills " + loadout.kills + " — style " + rank;
    }
}
