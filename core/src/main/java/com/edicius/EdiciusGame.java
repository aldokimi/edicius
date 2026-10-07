package com.edicius;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.edicius.meta.MetaProgress;
import com.edicius.run.RunSession;
import com.edicius.screen.BootScreen;

public class EdiciusGame extends Game {

    public SpriteBatch batch;
    public BitmapFont font;
    public MetaProgress meta;
    public RunSession session;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        meta = MetaProgress.load();
        setScreen(new BootScreen(this));
    }

    @Override
    public void dispose() {
        if (screen != null) {
            screen.dispose();
        }
        batch.dispose();
        font.dispose();
        if (meta != null) {
            meta.save();
        }
    }
}
