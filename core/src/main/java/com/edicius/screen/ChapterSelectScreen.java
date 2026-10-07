package com.edicius.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.edicius.EdiciusGame;
import com.edicius.content.ChapterDef;
import com.edicius.content.ContentLibrary;
import com.edicius.run.RunSession;

import java.util.List;

public class ChapterSelectScreen extends ScreenAdapter {

    private final EdiciusGame game;
    private final List<ChapterDef> chapters = ContentLibrary.chapters();

    public ChapterSelectScreen(EdiciusGame game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.04f, 0.05f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.font.setColor(Color.CYAN);
        game.font.getData().setScale(1.5f);
        game.font.draw(game.batch, "SELECT SECTOR", 80, 640);
        game.font.getData().setScale(1.1f);
        float y = 560;
        for (int i = 0; i < chapters.size(); i++) {
            ChapterDef c = chapters.get(i);
            boolean unlocked = game.meta.chapterUnlocked(c.id);
            game.font.setColor(unlocked ? Color.WHITE : Color.GRAY);
            game.font.draw(game.batch, (i + 1) + "  " + c.name + (unlocked ? "" : "  [LOCKED]"), 80, y);
            y -= 40;
        }
        game.font.setColor(Color.LIGHT_GRAY);
        game.font.draw(game.batch, "ESC back", 80, 120);
        game.batch.end();

        for (int i = 0; i < chapters.size(); i++) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i) && game.meta.chapterUnlocked(chapters.get(i).id)) {
                final var chapter = chapters.get(i);
                game.setScreen(new CharacterSelectScreen(game, () -> {
                    game.session = RunSession.campaign(chapter, game.meta.createLoadout(), System.nanoTime());
                    game.setScreen(new BriefScreen(game));
                }));
                return;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }
    }
}
