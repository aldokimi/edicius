package com.edicius.net;

import com.edicius.meta.Leaderboard;
import com.edicius.meta.MetaProgress;

import java.util.List;

public class LocalLeaderboardClient implements LeaderboardClient {

    private final MetaProgress meta;

    public LocalLeaderboardClient(MetaProgress meta) {
        this.meta = meta;
    }

    @Override
    public void submit(String label, int score, long seed) {
        meta.leaderboard.submit(label, score, seed);
        meta.save();
    }

    @Override
    public List<Leaderboard.Entry> top() {
        return meta.leaderboard.top();
    }
}
