package com.edicius.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.edicius.EdiciusGame;
import com.edicius.meta.Leaderboard;

public class MetaScreen extends ScreenAdapter {

    private final EdiciusGame game;

    public MetaScreen(EdiciusGame game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.04f, 0.06f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.font.setColor(Color.CYAN);
        game.font.getData().setScale(1.5f);
        game.font.draw(game.batch, "PROTOCOL TREE", 80, 640);
        game.font.getData().setScale(1.1f);
        game.font.setColor(Color.WHITE);
        game.font.draw(game.batch, "Points: " + game.meta.protocolPoints, 80, 580);
        game.font.draw(game.batch, "1 Mobility " + game.meta.mobility, 80, 520);
        game.font.draw(game.batch, "2 Offense  " + game.meta.offense, 80, 480);
        game.font.draw(game.batch, "3 Defense  " + game.meta.defense, 80, 440);
        game.font.draw(game.batch, "4 Utility  " + game.meta.utility, 80, 400);
        game.font.draw(game.batch, "5 Cycle cosmetic trail (" + game.meta.cosmeticTrail + ")", 80, 360);
        game.font.setColor(Color.ORANGE);
        game.font.draw(game.batch, "Local leaderboard", 80, 300);
        float y = 260;
        game.font.setColor(Color.LIGHT_GRAY);
        for (Leaderboard.Entry e : game.meta.leaderboard.top()) {
            game.font.draw(game.batch, e.label + "  score " + e.score + "  seed " + e.seed, 100, y);
            y -= 24;
            if (y < 80) {
                break;
            }
        }
        game.font.draw(game.batch, "ESC back", 80, 40);
        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) game.meta.spend("mobility");
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) game.meta.spend("offense");
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) game.meta.spend("defense");
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) game.meta.spend("utility");
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) game.meta.cycleCosmetic();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }
    }
}
