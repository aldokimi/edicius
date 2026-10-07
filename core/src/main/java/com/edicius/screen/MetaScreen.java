package com.edicius.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.edicius.EdiciusGame;
import com.edicius.meta.Leaderboard;
import com.edicius.ui.MenuStageScreen;

public class MetaScreen extends MenuStageScreen {

    public MetaScreen(EdiciusGame game) {
        super(game);
    }

    @Override
    protected void buildUi(Table root) {
        root.pad(40, 70, 40, 70).top().left();
        root.add(new Label("PROTOCOL TREE", game.ui.skin, "heading")).left().padBottom(8).row();
        root.add(new Label("Points: " + game.meta.protocolPoints, game.ui.skin, "accent")).left().padBottom(18).row();

        addSpend(root, "Mobility " + game.meta.mobility, "mobility");
        addSpend(root, "Offense " + game.meta.offense, "offense");
        addSpend(root, "Defense " + game.meta.defense, "defense");
        addSpend(root, "Utility " + game.meta.utility, "utility");

        TextButton cosmetic = new TextButton("Cycle cosmetic (" + game.meta.cosmeticTrail + ")", game.ui.skin);
        cosmetic.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.meta.cycleCosmetic();
                refresh();
            }
        });
        root.add(cosmetic).width(480).height(46).left().padBottom(16).row();

        root.add(new Label("Local leaderboard", game.ui.skin, "accent")).left().padBottom(6).row();
        for (Leaderboard.Entry e : game.leaderboards.top()) {
            root.add(new Label(e.label + "  score " + e.score + "  seed " + e.seed, game.ui.skin, "muted"))
                    .left().padBottom(2).row();
        }
        root.add(new Label("Death log rooms: " + game.meta.deathLog.snapshot().size(), game.ui.skin, "muted"))
                .left().padTop(10).row();

        TextButton back = new TextButton("Back", game.ui.skin, "danger");
        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        root.add(back).width(180).height(44).left().padTop(20).row();
    }

    private void addSpend(Table root, String label, String branch) {
        TextButton btn = new TextButton(label, game.ui.skin);
        btn.setDisabled(game.meta.protocolPoints <= 0);
        btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (game.meta.spend(branch)) {
                    refresh();
                }
            }
        });
        root.add(btn).width(420).height(44).left().padBottom(8).row();
    }

    private void refresh() {
        root.clear();
        buildUi(root);
    }

    @Override
    protected void handleShortcuts() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) && game.meta.spend("mobility")) refresh();
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2) && game.meta.spend("offense")) refresh();
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3) && game.meta.spend("defense")) refresh();
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4) && game.meta.spend("utility")) refresh();
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            game.meta.cycleCosmetic();
            refresh();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }
    }
}
