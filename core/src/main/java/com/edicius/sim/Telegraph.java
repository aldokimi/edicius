package com.edicius.sim;

public class Telegraph {

    public final float x;
    public final float y;
    public final float radius;
    public final int damage;
    public float timeLeft;
    public final float duration;
    public boolean resolved;

    public Telegraph(float x, float y, float radius, float duration, int damage) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.duration = duration;
        this.timeLeft = duration;
        this.damage = damage;
    }

    public void update(float dt) {
        timeLeft -= dt;
    }

    public boolean isReady() {
        return timeLeft <= 0f && !resolved;
    }

    public float progress() {
        return 1f - Math.max(0f, timeLeft) / duration;
    }
}
