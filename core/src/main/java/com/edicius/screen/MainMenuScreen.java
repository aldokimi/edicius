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
        game.font.setColor(Color.CYAN);
        game.font.getData().setScale(2f);
        game.font.draw(game.batch, "EDICIUS — NEON PROTOCOL", 60, 640);
        game.font.getData().setScale(1.15f);
        game.font.setColor(Color.WHITE);
        game.font.draw(game.batch, "1  Campaign chapters", 80, 540);
        game.font.draw(game.batch, "2  Daily Protocol (seed " + DailySeed.today() + ")", 80, 500);
        game.font.draw(game.batch, "3  Endless Protocol" + (game.meta.endlessUnlocked ? "" : " (clear Act 3)"), 80, 460);
        game.font.draw(game.batch, "4  Meta / Protocol tree", 80, 420);
        game.font.draw(game.batch, "5  Codex", 80, 380);
        game.font.draw(game.batch, "Points: " + game.meta.protocolPoints
                + "  Trail: " + game.meta.cosmeticTrail, 80, 320);
        if (status != null && !status.isBlank()) {
            game.font.setColor(Color.ORANGE);
            game.font.draw(game.batch, status, 80, 260);
        }
        game.batch.end();

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
