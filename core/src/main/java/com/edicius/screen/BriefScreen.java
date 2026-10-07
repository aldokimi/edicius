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
        if (game.assets.menuBg != null) {
            game.batch.setColor(0.35f, 0.25f, 0.55f, 0.45f);
            game.batch.draw(game.assets.menuBg, 0, 0, 1280, 720);
            game.batch.setColor(Color.WHITE);
        }
        game.font.setColor(Color.MAGENTA);
        game.font.getData().setScale(1.6f);
        game.font.draw(game.batch, "NYX // BRIEF", 80, 620);
        if (game.session.chapter != null) {
            game.font.setColor(Color.CYAN);
            game.font.getData().setScale(1.2f);
            game.font.draw(game.batch, game.session.chapter.name, 80, 570);
        }
        game.font.setColor(Color.WHITE);
        game.font.getData().setScale(1.1f);
        wrap(game.session.lastNyxLine, 80, 500, 36);
        game.font.setColor(game.meta.chroma.tint);
        game.font.draw(game.batch, "Shell: " + game.meta.selectedCharacterId
                + "  ·  Chroma: " + game.meta.chroma.label, 80, 300);
        if (game.session.activeMutator != null) {
            game.font.setColor(Color.ORANGE);
            game.font.draw(game.batch, "Mutator: " + game.session.activeMutator.label, 80, 260);
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
            if (line.length() + w.length() > 68) {
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
