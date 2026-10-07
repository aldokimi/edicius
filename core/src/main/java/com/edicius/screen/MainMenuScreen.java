package com.edicius.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.edicius.EdiciusGame;

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
        game.font.draw(game.batch, "EDICIUS — NEON PROTOCOL", 80, 480);
        game.font.getData().setScale(1.2f);
        game.font.setColor(Color.WHITE);
        game.font.draw(game.batch, "ENTER or CLICK — Start raid", 80, 400);
        if (status != null && !status.isBlank()) {
            game.font.setColor(Color.ORANGE);
            game.font.draw(game.batch, status, 80, 340);
        }
        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.justTouched()) {
            game.setScreen(new RunScreen(game));
        }
    }
}
