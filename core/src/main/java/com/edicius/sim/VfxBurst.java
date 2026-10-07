package com.edicius.sim;

public class VfxBurst {

    public final float x;
    public final float y;
    public final float r;
    public final float g;
    public final float b;
    public float life;
    public final float maxLife;

    public VfxBurst(float x, float y, float life, float r, float g, float b) {
        this.x = x;
        this.y = y;
        this.life = life;
        this.maxLife = life;
        this.r = r;
        this.g = g;
        this.b = b;
    }
}
