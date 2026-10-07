package com.edicius;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.edicius.audio.AudioBus;
import com.edicius.gfx.GameAssets;
import com.edicius.meta.MetaProgress;
import com.edicius.net.LeaderboardClient;
import com.edicius.net.LocalLeaderboardClient;
import com.edicius.run.RunSession;
import com.edicius.screen.BootScreen;

public class EdiciusGame extends Game {

    public SpriteBatch batch;
    public BitmapFont font;
    public MetaProgress meta;
    public RunSession session;
    public GameAssets assets;
    public AudioBus audio;
    public LeaderboardClient leaderboards;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(1.05f);
        assets = new GameAssets();
        assets.load();
        audio = new AudioBus();
        audio.load();
        audio.startMusic();
        meta = MetaProgress.load();
        leaderboards = new LocalLeaderboardClient(meta);
        setScreen(new BootScreen(this));
    }

    @Override
    public void dispose() {
        if (screen != null) {
            screen.dispose();
        }
        if (audio != null) {
            audio.dispose();
        }
        if (assets != null) {
            assets.dispose();
        }
        batch.dispose();
        font.dispose();
        if (meta != null) {
            meta.save();
        }
    }
}
