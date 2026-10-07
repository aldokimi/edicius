package com.edicius.gfx;

public enum Facing {
    S(0), SW(1), W(2), NW(3), N(4), NE(5), E(6), SE(7);

    public final int row;

    Facing(int row) {
        this.row = row;
    }

    /** LibGDX world space: +Y up, +X right. */
    public static Facing fromVector(float dx, float dy) {
        if (dx == 0f && dy == 0f) {
            return S;
        }
        if (Math.abs(dx) < 0.01f) {
            return dy > 0 ? N : S;
        }
        if (Math.abs(dy) < 0.01f) {
            return dx > 0 ? E : W;
        }
        if (dx < 0 && dy > 0) return NW;
        if (dx < 0 && dy < 0) return SW;
        if (dx > 0 && dy > 0) return NE;
        return SE;
    }
}
