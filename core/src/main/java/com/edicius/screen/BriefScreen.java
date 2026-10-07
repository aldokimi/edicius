package com.edicius.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.edicius.EdiciusGame;

public class BriefScreen extends ScreenAdapter {

    private final EdiciusGame game;

    public BriefScreen(EdiciusGame game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.03f, 0.04f, 0.09f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.font.setColor(Color.MAGENTA);
        game.font.getData().setScale(1.4f);
        game.font.draw(game.batch, "NYX // BRIEF", 80, 620);
        game.font.setColor(Color.WHITE);
        game.font.getData().setScale(1.1f);
        wrap(game.session.lastNyxLine, 80, 540, 40);
        if (game.session.activeMutator != null) {
            game.font.setColor(Color.ORANGE);
            game.font.draw(game.batch, "Mutator: " + game.session.activeMutator.label, 80, 280);
        }
        game.font.setColor(Color.CYAN);
        game.font.draw(game.batch, "ENTER — Drop in", 80, 180);
        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.justTouched()) {
            game.setScreen(new RunScreen(game));
        }
    }

    private void wrap(String text, float x, float y, float lineH) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        float cy = y;
        for (String w : words) {
            if (line.length() + w.length() > 70) {
                game.font.draw(game.batch, line.toString(), x, cy);
                cy -= lineH;
                line = new StringBuilder();
            }
            if (line.length() > 0) {
                line.append(' ');
            }
            line.append(w);
        }
        if (line.length() > 0) {
            game.font.draw(game.batch, line.toString(), x, cy);
        }
    }
}
