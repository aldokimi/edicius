package com.edicius.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.edicius.EdiciusGame;
import com.edicius.content.ChapterDef;
import com.edicius.content.ContentLibrary;
import com.edicius.run.RunSession;
import com.edicius.ui.MenuStageScreen;

import java.util.List;

public class ChapterSelectScreen extends MenuStageScreen {

    private final List<ChapterDef> chapters = ContentLibrary.chapters();

    public ChapterSelectScreen(EdiciusGame game) {
        super(game);
    }

    @Override
    protected void buildUi(Table root) {
        root.pad(48, 70, 40, 70).top().left();
        root.add(new Label("SELECT SECTOR", game.ui.skin, "heading")).left().padBottom(24).row();

        for (int i = 0; i < chapters.size(); i++) {
            ChapterDef c = chapters.get(i);
            boolean unlocked = game.meta.chapterUnlocked(c.id);
            String label = (i + 1) + "  " + c.name + (unlocked ? "" : "  [LOCKED]");
            TextButton btn = new TextButton(label, game.ui.skin);
            btn.setDisabled(!unlocked);
            final ChapterDef chapter = c;
            btn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    startChapter(chapter);
                }
            });
            root.add(btn).width(640).height(52).left().padBottom(12).row();
        }

        TextButton back = new TextButton("Back", game.ui.skin, "danger");
        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        root.add(back).width(200).height(44).left().padTop(28).row();
    }

    private void startChapter(ChapterDef chapter) {
        if (!game.meta.chapterUnlocked(chapter.id)) {
            return;
        }
        game.setScreen(new CharacterSelectScreen(game, () -> {
            game.session = RunSession.campaign(chapter, game.meta.createLoadout(), System.nanoTime());
            game.setScreen(new BriefScreen(game));
        }));
    }

    @Override
    protected void handleShortcuts() {
        for (int i = 0; i < chapters.size(); i++) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i) && game.meta.chapterUnlocked(chapters.get(i).id)) {
                startChapter(chapters.get(i));
                return;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }
    }
}
