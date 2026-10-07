package com.edicius.gfx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SheetAnimator {

    public static final int FRAME = 64;

    private final Texture stand;
    private final Texture walk;
    private int frameIndex;
    private float frameTime;
    private final float secondsPerFrame;
    private boolean walking;
    private Facing facing = Facing.S;

    public SheetAnimator(Texture stand, Texture walk, float secondsPerFrame) {
        this.stand = stand;
        this.walk = walk;
        this.secondsPerFrame = secondsPerFrame;
    }

    public void update(float dt, Facing facing, boolean moving) {
        this.facing = facing;
        this.walking = moving;
        if (!moving) {
            frameIndex = 0;
            frameTime = 0f;
            return;
        }
        frameTime += dt;
        int cols = Math.max(1, walk.getWidth() / FRAME);
        if (frameTime >= secondsPerFrame) {
            frameTime = 0f;
            frameIndex = (frameIndex + 1) % cols;
        }
    }

    public TextureRegion region() {
        Texture sheet = walking ? walk : stand;
        int cols = Math.max(1, sheet.getWidth() / FRAME);
        int rows = Math.max(1, sheet.getHeight() / FRAME);
        int r = Math.min(facing.row, rows - 1);
        int c = Math.min(frameIndex, cols - 1);
        return new TextureRegion(sheet, c * FRAME, r * FRAME, FRAME, FRAME);
    }
}
