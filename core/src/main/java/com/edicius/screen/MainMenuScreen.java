package com.edicius.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.edicius.EdiciusGame;
import com.edicius.meta.DailySeed;
import com.edicius.run.RunSession;

public class MainMenuScreen extends ScreenAdapter {

    private final EdiciusGame game;
    private final String status;

    public MainMenuScreen(EdiciusGame game) {
        this(game, null);
    }

    public MainMenuScreen(EdiciusGame game, String status) {
        this.game = game;
        this.status = status;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.12f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        if (game.assets.menuBg != null) {
            game.batch.setColor(1f, 1f, 1f, 0.55f);
            game.batch.draw(game.assets.menuBg, 0, 0, 1280, 720);
            game.batch.setColor(Color.WHITE);
        }
        game.font.setColor(Color.CYAN);
        game.font.getData().setScale(2.2f);
        game.font.draw(game.batch, "EDICIUS", 70, 640);
        game.font.getData().setScale(1.3f);
        game.font.setColor(new Color(0.7f, 0.4f, 1f, 1f));
        game.font.draw(game.batch, "NEON PROTOCOL", 74, 600);
        game.font.getData().setScale(1.15f);
        game.font.setColor(Color.WHITE);
        game.font.draw(game.batch, "1  Campaign chapters", 80, 500);
        game.font.draw(game.batch, "2  Daily Protocol (seed " + DailySeed.today() + ")", 80, 460);
        game.font.draw(game.batch, "3  Endless Protocol" + (game.meta.endlessUnlocked ? "" : " (clear Act 3)"), 80, 420);
        game.font.draw(game.batch, "4  Meta / Protocol tree", 80, 380);
        game.font.draw(game.batch, "5  Codex", 80, 340);
        game.font.setColor(Color.LIGHT_GRAY);
        game.font.draw(game.batch, "Points: " + game.meta.protocolPoints
                + "   Trail: " + game.meta.cosmeticTrail
                + "   M mute", 80, 280);
        if (status != null && !status.isBlank()) {
            game.font.setColor(Color.ORANGE);
            game.font.draw(game.batch, status, 80, 230);
        }
        game.font.setColor(new Color(0.5f, 0.9f, 1f, 1f));
        game.font.getData().setScale(0.95f);
        game.font.draw(game.batch, "Stylish outlaw AI vs THE GRID — desktop raid protocol", 80, 120);
        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            game.audio.toggleMute();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new ChapterSelectScreen(game));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            game.session = RunSession.daily(game.meta.createLoadout(), DailySeed.today());
            game.setScreen(new BriefScreen(game));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3) && game.meta.endlessUnlocked) {
            game.session = RunSession.endless(game.meta.createLoadout(), System.nanoTime());
            game.setScreen(new BriefScreen(game));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            game.setScreen(new MetaScreen(game));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            game.setScreen(new CodexScreen(game));
        }
    }
}
