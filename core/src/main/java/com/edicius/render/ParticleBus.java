package com.edicius.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.edicius.gfx.GameAssets;

import java.util.ArrayList;
import java.util.List;

/** Pooled spark / trail particles for combat juice. */
public class ParticleBus {

    private static final int CAP = 360;

    private final List<P> live = new ArrayList<>(CAP);
    private final List<P> pool = new ArrayList<>(CAP);

    public void muzzle(float x, float y, float angleDeg) {
        float rad = (float) Math.toRadians(angleDeg);
        for (int i = 0; i < 8; i++) {
            float spread = (i - 4) * 0.08f;
            spawn(x, y,
                    (float) Math.cos(rad + spread) * (180 + i * 20),
                    (float) Math.sin(rad + spread) * (180 + i * 20),
                    0.18f + i * 0.01f, 1f, 0.9f, 0.35f, 18f, Kind.MUZZLE);
        }
    }

    public void hit(float x, float y) {
        for (int i = 0; i < 14; i++) {
            float a = (float) (Math.random() * Math.PI * 2);
            float sp = 80f + (float) Math.random() * 220f;
            spawn(x, y, (float) Math.cos(a) * sp, (float) Math.sin(a) * sp,
                    0.25f + (float) Math.random() * 0.2f, 1f, 0.55f, 0.2f, 22f, Kind.HIT);
        }
    }

    public void death(float x, float y) {
        for (int i = 0; i < 28; i++) {
            float a = (float) (Math.random() * Math.PI * 2);
            float sp = 40f + (float) Math.random() * 280f;
            spawn(x, y, (float) Math.cos(a) * sp, (float) Math.sin(a) * sp,
                    0.45f + (float) Math.random() * 0.25f, 0.4f, 1f, 0.95f, 36f, Kind.DEATH);
        }
    }

    public void dash(float x, float y, float vx, float vy) {
        for (int i = 0; i < 10; i++) {
            spawn(x - vx * 0.02f * i, y - vy * 0.02f * i,
                    -vx * 0.15f + (float) (Math.random() - 0.5) * 40f,
                    -vy * 0.15f + (float) (Math.random() - 0.5) * 40f,
                    0.22f, 0.35f, 0.95f, 1f, 20f, Kind.DASH);
        }
    }

    public void roomClear(float x, float y) {
        for (int i = 0; i < 40; i++) {
            float a = (float) (Math.random() * Math.PI * 2);
            float sp = 60f + (float) Math.random() * 340f;
            spawn(x, y, (float) Math.cos(a) * sp, (float) Math.sin(a) * sp,
                    0.7f, 0.4f, 1f, 1f, 48f, Kind.CLEAR);
        }
    }

    private void spawn(float x, float y, float vx, float vy, float life, float r, float g, float b, float size, Kind kind) {
        if (live.size() >= CAP) {
            return;
        }
        P p = pool.isEmpty() ? new P() : pool.remove(pool.size() - 1);
        p.x = x;
        p.y = y;
        p.vx = vx;
        p.vy = vy;
        p.life = life;
        p.maxLife = life;
        p.r = r;
        p.g = g;
        p.b = b;
        p.size = size;
        p.kind = kind;
        live.add(p);
    }

    public void update(float dt) {
        for (int i = live.size() - 1; i >= 0; i--) {
            P p = live.get(i);
            p.life -= dt;
            p.x += p.vx * dt;
            p.y += p.vy * dt;
            p.vx *= 0.92f;
            p.vy *= 0.92f;
            if (p.life <= 0f) {
                live.remove(i);
                pool.add(p);
            }
        }
    }

    public void draw(SpriteBatch batch, GameAssets assets) {
        for (P p : live) {
            float alpha = Math.max(0f, p.life / p.maxLife);
            batch.setColor(p.r, p.g, p.b, alpha);
            Texture tex = switch (p.kind) {
                case MUZZLE -> assets.vfxMuzzle;
                case HIT -> assets.vfxSpark;
                case DEATH -> assets.vfxStar;
                case DASH -> assets.vfxFlare;
                case CLEAR -> assets.vfxCircle;
            };
            float s = p.size * (0.6f + 0.8f * alpha);
            batch.draw(tex, p.x - s * 0.5f, p.y - s * 0.5f, s, s);
        }
        batch.setColor(Color.WHITE);
    }

    private enum Kind { MUZZLE, HIT, DEATH, DASH, CLEAR }

    private static final class P {
        float x, y, vx, vy, life, maxLife, r, g, b, size;
        Kind kind;
    }
}
