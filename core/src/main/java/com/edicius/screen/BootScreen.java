package com.edicius.screen;

import com.badlogic.gdx.ScreenAdapter;
import com.edicius.EdiciusGame;

public class BootScreen extends ScreenAdapter {

    private final EdiciusGame game;

    public BootScreen(EdiciusGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        game.setScreen(new MainMenuScreen(game));
    }
}
