package com.edicius.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.edicius.EdiciusGame;
import com.edicius.meta.Codex;

import java.util.Map;

public class CodexScreen extends ScreenAdapter {

    private final EdiciusGame game;

    public CodexScreen(EdiciusGame game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.03f, 0.03f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.font.setColor(Color.CYAN);
        game.font.getData().setScale(1.5f);
        game.font.draw(game.batch, "CODEX", 80, 640);
        game.font.getData().setScale(1.0f);
        float y = 580;
        for (Map.Entry<String, String> e : Codex.entries().entrySet()) {
            boolean unlocked = game.meta.codex.contains(e.getKey());
            game.font.setColor(unlocked ? Color.WHITE : Color.DARK_GRAY);
            game.font.draw(game.batch, unlocked ? e.getValue() : "[LOCKED] " + e.getKey(), 80, y);
            y -= 36;
        }
        game.font.setColor(Color.LIGHT_GRAY);
        game.font.draw(game.batch, "ESC back", 80, 40);
        game.batch.end();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }
    }
}
