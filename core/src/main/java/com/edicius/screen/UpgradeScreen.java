package com.edicius.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.edicius.EdiciusGame;
import com.edicius.run.Upgrade;

import java.util.List;

public class UpgradeScreen extends ScreenAdapter {

    private final EdiciusGame game;
    private final List<Upgrade> choices;

    public UpgradeScreen(EdiciusGame game) {
        this.game = game;
        this.choices = game.session.rollUpgrades();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.06f, 0.04f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.font.setColor(Color.CYAN);
        game.font.getData().setScale(1.5f);
        game.font.draw(game.batch, "CHOOSE A PROTOCOL UPGRADE", 80, 600);
        game.font.getData().setScale(1.15f);
        float y = 500;
        for (int i = 0; i < choices.size(); i++) {
            game.font.setColor(Color.WHITE);
            game.font.draw(game.batch, (i + 1) + "  " + choices.get(i).name() + " — " + choices.get(i).description, 80, y);
            y -= 50;
        }
        game.batch.end();

        for (int i = 0; i < choices.size(); i++) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i)) {
                Upgrade u = choices.get(i);
                u.applier.apply(game.session.loadout);
                if (u == Upgrade.SECOND_CORE) {
                    game.session.loadout.beamUnlocked = true;
                    game.meta.beamUnlocked = true;
                }
                game.session.advanceRoom();
                game.setScreen(new RunScreen(game));
                return;
            }
        }
    }
}
