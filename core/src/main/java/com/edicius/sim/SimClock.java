package com.edicius.sim;

public class SimClock {

    public static final float TICK = 1f / 60f;

    private float accumulator;

    public void accumulate(float deltaSeconds) {
        if (deltaSeconds < 0f) {
            return;
        }
        accumulator += deltaSeconds;
    }

    public boolean consumeTick() {
        if (accumulator >= TICK) {
            accumulator -= TICK;
            return true;
        }
        return false;
    }

    public float getAccumulator() {
        return accumulator;
    }
}
