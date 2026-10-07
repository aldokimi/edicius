package com.edicius.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.edicius.EdiciusGame;
import com.edicius.meta.Codex;
import com.edicius.ui.MenuStageScreen;

import java.util.Map;

public class CodexScreen extends MenuStageScreen {

    public CodexScreen(EdiciusGame game) {
        super(game);
    }

    @Override
    protected void buildUi(Table root) {
        root.pad(40, 70, 40, 70).top().left();
        root.add(new Label("CODEX", game.ui.skin, "heading")).left().padBottom(16).row();

        Table list = new Table();
        list.top().left();
        for (Map.Entry<String, String> e : Codex.entries().entrySet()) {
            boolean unlocked = game.meta.codex.contains(e.getKey());
            Label row = new Label(unlocked ? e.getValue() : "[LOCKED] " + e.getKey(), game.ui.skin,
                    unlocked ? "default" : "muted");
            row.setWrap(true);
            row.setAlignment(Align.left);
            list.add(row).width(980).left().padBottom(14).row();
        }
        ScrollPane scroll = new ScrollPane(list);
        scroll.setFadeScrollBars(false);
        root.add(scroll).width(1000).height(480).left().row();

        TextButton back = new TextButton("Back", game.ui.skin, "danger");
        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        root.add(back).width(180).height(44).left().padTop(16).row();
    }

    @Override
    protected void handleShortcuts() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }
    }
}
