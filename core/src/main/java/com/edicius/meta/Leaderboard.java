package com.edicius.meta;

import com.badlogic.gdx.Preferences;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Leaderboard {

    public static class Entry {
        public final String label;
        public final int score;
        public final long seed;

        public Entry(String label, int score, long seed) {
            this.label = label;
            this.score = score;
            this.seed = seed;
        }
    }

    private final List<Entry> entries = new ArrayList<>();

    public void load(Preferences p) {
        entries.clear();
        int n = p.getInteger("lb_n", 0);
        for (int i = 0; i < n; i++) {
            entries.add(new Entry(
                    p.getString("lb_l_" + i, "run"),
                    p.getInteger("lb_s_" + i, 0),
                    p.getLong("lb_seed_" + i, 0L)
            ));
        }
        sort();
    }

    public void save(Preferences p) {
        p.putInteger("lb_n", entries.size());
        for (int i = 0; i < entries.size(); i++) {
            Entry e = entries.get(i);
            p.putString("lb_l_" + i, e.label);
            p.putInteger("lb_s_" + i, e.score);
            p.putLong("lb_seed_" + i, e.seed);
        }
    }

    public void submit(String label, int score, long seed) {
        entries.add(new Entry(label, score, seed));
        sort();
        if (entries.size() > 10) {
            entries.subList(10, entries.size()).clear();
        }
    }

    private void sort() {
        entries.sort(Comparator.comparingInt((Entry e) -> e.score).reversed());
    }

    public List<Entry> top() {
        return List.copyOf(entries);
    }
}
