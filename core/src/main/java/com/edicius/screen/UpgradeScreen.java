package com.edicius.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.edicius.EdiciusGame;
import com.edicius.run.Upgrade;
import com.edicius.ui.MenuStageScreen;

import java.util.List;

public class UpgradeScreen extends MenuStageScreen {

    private final List<Upgrade> choices;
    private boolean picked;

    public UpgradeScreen(EdiciusGame game) {
        super(game);
        this.choices = game.session.rollUpgrades();
        // Space must not auto-click the focused upgrade button.
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                return keycode == Input.Keys.SPACE;
            }
        });
    }

    @Override
    protected void buildUi(Table root) {
        root.pad(48, 70, 40, 70).top().left();
        root.add(new Label("CHOOSE A PROTOCOL UPGRADE", game.ui.skin, "heading")).left().padBottom(28).row();

        for (int i = 0; i < choices.size(); i++) {
            Upgrade u = choices.get(i);
            TextButton btn = new TextButton((i + 1) + "  " + u.name() + " — " + u.description, game.ui.skin);
            btn.getLabel().setWrap(true);
            final Upgrade pick = u;
            btn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    pickUpgrade(pick);
                }
            });
            root.add(btn).width(980).height(64).left().padBottom(14).row();
        }
    }

    private void pickUpgrade(Upgrade u) {
        if (picked) {
            return;
        }
        picked = true;
        u.applier.apply(game.session.loadout);
        if (u == Upgrade.SECOND_CORE) {
            game.session.loadout.beamUnlocked = true;
            game.meta.beamUnlocked = true;
        }
        game.session.advanceRoom();
        game.setScreen(new RunScreen(game));
    }

    @Override
    protected void handleShortcuts() {
        for (int i = 0; i < choices.size(); i++) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i)) {
                pickUpgrade(choices.get(i));
                return;
            }
        }
    }
}
