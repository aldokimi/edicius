package com.edicius.net;

import com.edicius.meta.Leaderboard;

/**
 * Phase-7 hook: swap LocalLeaderboardClient for an HTTP implementation later.
 */
public interface LeaderboardClient {
    void submit(String label, int score, long seed);

    java.util.List<Leaderboard.Entry> top();
}
