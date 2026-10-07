package com.edicius.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Disposable;

/** Lightweight additive bloom via FBO + offset passes (no custom shader). */
public class BloomEffect implements Disposable {

    private FrameBuffer fbo;
    private int w = -1;
    private int h = -1;
    public float intensity = 0.55f;

    public void begin(int width, int height) {
        ensure(width, height);
        fbo.begin();
        Gdx.gl.glClearColor(0.02f, 0.02f, 0.06f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public void endAndDraw(SpriteBatch batch, OrthographicCamera screenCam) {
        fbo.end();
        Texture tex = fbo.getColorBufferTexture();
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        batch.setProjectionMatrix(screenCam.combined);
        batch.begin();
        float sw = screenCam.viewportWidth;
        float sh = screenCam.viewportHeight;
        int tw = tex.getWidth();
        int th = tex.getHeight();
        batch.setColor(1f, 1f, 1f, 1f);
        // FBO color buffer is vertically flipped
        batch.draw(tex, 0, 0, sw, sh, 0, 0, tw, th, false, true);

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        float a = intensity;
        batch.setColor(0.55f, 0.85f, 1f, a * 0.35f);
        batch.draw(tex, -3, -3, sw + 6, sh + 6, 0, 0, tw, th, false, true);
        batch.setColor(1f, 0.55f, 0.9f, a * 0.22f);
        batch.draw(tex, -6, -6, sw + 12, sh + 12, 0, 0, tw, th, false, true);
        batch.setColor(1f, 1f, 1f, a * 0.18f);
        batch.draw(tex, 2, 2, sw - 4, sh - 4, 0, 0, tw, th, false, true);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();
    }

    private void ensure(int width, int height) {
        if (fbo != null && w == width && h == height) {
            return;
        }
        if (fbo != null) {
            fbo.dispose();
        }
        w = Math.max(2, width);
        h = Math.max(2, height);
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, w, h, false);
    }

    @Override
    public void dispose() {
        if (fbo != null) {
            fbo.dispose();
            fbo = null;
        }
    }
}
