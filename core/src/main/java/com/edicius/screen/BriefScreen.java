package com.edicius.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.edicius.EdiciusGame;
import com.edicius.ui.MenuStageScreen;

public class BriefScreen extends MenuStageScreen {

    public BriefScreen(EdiciusGame game) {
        super(game);
    }

    @Override
    protected void buildUi(Table root) {
        root.pad(48, 70, 40, 70).top().left();
        Label nyx = new Label("NYX // BRIEF", game.ui.skin, "heading");
        nyx.setColor(1f, 0.35f, 0.85f, 1f);
        root.add(nyx).left().padBottom(12).row();

        if (game.session.chapter != null) {
            root.add(new Label(game.session.chapter.name, game.ui.skin, "heading")).left().padBottom(16).row();
        }

        Label brief = new Label(game.session.lastNyxLine, game.ui.skin, "default");
        brief.setWrap(true);
        brief.setAlignment(Align.left);
        root.add(brief).width(900).left().padBottom(20).row();

        root.add(new Label(
                "Shell: " + game.meta.selectedCharacterId + "   ·   Chroma: " + game.meta.chroma.label,
                game.ui.skin, "accent")).left().padBottom(12).row();

        if (game.session.activeMutator != null) {
            root.add(new Label("Mutator: " + game.session.activeMutator.label, game.ui.skin, "accent"))
                    .left().padBottom(24).row();
        }

        Table row = new Table();
        TextButton drop = new TextButton("Drop in", game.ui.skin);
        drop.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dropIn();
            }
        });
        TextButton back = new TextButton("Back", game.ui.skin, "danger");
        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        row.add(drop).width(220).height(48).padRight(16);
        row.add(back).width(160).height(48);
        root.add(row).left().padTop(12).row();
    }

    private void dropIn() {
        game.setScreen(new RunScreen(game));
    }

    @Override
    protected void handleShortcuts() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            dropIn();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }
    }
}
