package com.edicius.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.edicius.EdiciusGame;
import com.edicius.gfx.CharacterCatalog;
import com.edicius.gfx.CharacterShell;
import com.edicius.gfx.ChromaPreset;
import com.edicius.gfx.UnitSkin;

import java.util.List;

public class CharacterSelectScreen extends ScreenAdapter {

    private final EdiciusGame game;
    private final Runnable onConfirm;
    private int index;

    public CharacterSelectScreen(EdiciusGame game, Runnable onConfirm) {
        this.game = game;
        this.onConfirm = onConfirm;
        List<CharacterShell> shells = CharacterCatalog.SHELLS;
        for (int i = 0; i < shells.size(); i++) {
            if (shells.get(i).id.equals(game.meta.selectedCharacterId)) {
                index = i;
                break;
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.04f, 0.05f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        List<CharacterShell> shells = CharacterCatalog.SHELLS;
        CharacterShell selected = shells.get(index);
        ChromaPreset chroma = game.meta.chroma;

        game.batch.begin();
        if (game.assets.menuBg != null) {
            game.batch.setColor(0.4f, 0.35f, 0.7f, 0.4f);
            game.batch.draw(game.assets.menuBg, 0, 0, 1280, 720);
            game.batch.setColor(Color.WHITE);
        }

        game.font.setColor(Color.CYAN);
        game.font.getData().setScale(1.6f);
        game.font.draw(game.batch, "SELECT PROTOCOL SHELL", 70, 660);
        game.font.getData().setScale(1.1f);
        game.font.setColor(Color.WHITE);
        game.font.draw(game.batch, "A/D or LEFT/RIGHT — shell   ·   Q/E — chroma   ·   ENTER confirm", 70, 620);

        float x = 70;
        for (int i = 0; i < shells.size(); i++) {
            CharacterShell shell = shells.get(i);
            UnitSkin skin = game.assets.playerSkin(shell.id);
            Texture preview = skin.armed != null ? skin.armed : skin.stand;
            boolean active = i == index;
            game.batch.setColor(active ? chroma.tint : new Color(0.55f, 0.55f, 0.6f, 1f));
            if (preview != null) {
                game.batch.draw(preview, x, 380, 96, 96);
            }
            game.font.getData().setScale(0.95f);
            game.font.setColor(active ? Color.CYAN : Color.LIGHT_GRAY);
            game.font.draw(game.batch, (i + 1) + " " + shell.displayName, x, 360);
            x += 190;
        }

        game.batch.setColor(Color.WHITE);
        game.font.getData().setScale(1.3f);
        game.font.setColor(chroma.tint);
        game.font.draw(game.batch, selected.displayName, 70, 280);
        game.font.getData().setScale(1.05f);
        game.font.setColor(Color.WHITE);
        game.font.draw(game.batch, selected.blurb, 70, 240);
        game.font.setColor(Color.ORANGE);
        game.font.draw(game.batch, "Chroma: " + chroma.label + "  (cosmetics only — no power change)", 70, 190);
        game.font.setColor(Color.LIGHT_GRAY);
        game.font.draw(game.batch, "ESC back", 70, 80);
        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            index = (index + 1) % shells.size();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            index = (index - 1 + shells.size()) % shells.size();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.E) || Gdx.input.isKeyJustPressed(Input.Keys.PERIOD)) {
            game.meta.cycleCosmetic();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q) || Gdx.input.isKeyJustPressed(Input.Keys.COMMA)) {
            game.meta.chroma = game.meta.chroma.prev();
            game.meta.cosmeticTrail = game.meta.chroma.name().toLowerCase();
            game.meta.save();
        }
        for (int i = 0; i < shells.size(); i++) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i)) {
                index = i;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.meta.selectCharacter(selected.id);
            onConfirm.run();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }
    }
}
