package com.edicius.meta;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.util.LinkedHashMap;
import java.util.Map;

/** Simple local death-by-room counters for balance review. */
public class DeathLog {

    private final Map<String, Integer> deaths = new LinkedHashMap<>();

    public void load(Preferences p) {
        deaths.clear();
        int n = p.getInteger("death_n", 0);
        for (int i = 0; i < n; i++) {
            deaths.put(p.getString("death_k_" + i, "?"), p.getInteger("death_v_" + i, 0));
        }
    }

    public void save(Preferences p) {
        p.putInteger("death_n", deaths.size());
        int i = 0;
        for (Map.Entry<String, Integer> e : deaths.entrySet()) {
            p.putString("death_k_" + i, e.getKey());
            p.putInteger("death_v_" + i, e.getValue());
            i++;
        }
    }

    public void record(String roomId) {
        deaths.merge(roomId, 1, Integer::sum);
        Gdx.app.log("DeathLog", roomId + " -> " + deaths.get(roomId));
    }

    public Map<String, Integer> snapshot() {
        return Map.copyOf(deaths);
    }
}
