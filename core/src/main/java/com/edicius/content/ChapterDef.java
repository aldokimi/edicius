package com.edicius.content;

import java.util.List;

public class ChapterDef {

    public final String id;
    public final String name;
    public final String nyxBrief;
    public final int actNumber;
    public final List<RoomDef> rooms;
    public final String unlocks;

    public ChapterDef(String id, String name, String nyxBrief, int actNumber, List<RoomDef> rooms, String unlocks) {
        this.id = id;
        this.name = name;
        this.nyxBrief = nyxBrief;
        this.actNumber = actNumber;
        this.rooms = rooms;
        this.unlocks = unlocks;
    }
}
