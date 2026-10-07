package com.edicius.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.edicius.EdiciusGame;
import com.edicius.meta.DailySeed;
import com.edicius.run.RunSession;
import com.edicius.ui.MenuStageScreen;

public class MainMenuScreen extends MenuStageScreen {

    private final String status;

    public MainMenuScreen(EdiciusGame game) {
        this(game, null);
    }

    public MainMenuScreen(EdiciusGame game, String status) {
        super(game);
        this.status = status;
    }

    @Override
    protected void buildUi(Table root) {
        root.pad(48, 70, 40, 70).top().left();

        root.add(new Label("EDICIUS", game.ui.skin, "title")).left().padBottom(4).row();
        Label sub = new Label("NEON PROTOCOL", game.ui.skin, "heading");
        sub.setColor(0.75f, 0.45f, 1f, 1f);
        root.add(sub).left().padBottom(28).row();

        addMenuButton(root, "Campaign chapters", this::openCampaign);
        addMenuButton(root, "Daily Protocol (seed " + DailySeed.today() + ")", this::openDaily);
        TextButton endless = addMenuButton(root, "Endless Protocol", this::openEndless);
        if (!game.meta.endlessUnlocked) {
            endless.setDisabled(true);
            endless.setText("Endless Protocol (clear Act 3)");
        }
        addMenuButton(root, "Meta / Protocol tree", () -> game.setScreen(new MetaScreen(game)));
        addMenuButton(root, "Codex", () -> game.setScreen(new CodexScreen(game)));
        addMenuButton(root, "Protocol shells / chroma", () ->
                game.setScreen(new CharacterSelectScreen(game, () -> game.setScreen(new MainMenuScreen(game)))));

        root.add(new Label(
                "Shell: " + game.meta.selectedCharacterId
                        + "   ·   Chroma: " + game.meta.chroma.label
                        + "   ·   Points: " + game.meta.protocolPoints,
                game.ui.skin, "muted")).left().padTop(24).row();

        if (status != null && !status.isBlank()) {
            root.add(new Label(status, game.ui.skin, "accent")).left().padTop(8).row();
        }

        root.add(new Label("Stylish outlaw AI vs THE GRID — click or use 1–6 / M mute", game.ui.skin, "muted"))
                .left().padTop(36).row();
    }

    private TextButton addMenuButton(Table root, String text, Runnable action) {
        TextButton btn = new TextButton(text, game.ui.skin);
        btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                action.run();
            }
        });
        root.add(btn).width(520).height(48).left().padBottom(10).row();
        return btn;
    }

    private void openCampaign() {
        game.setScreen(new ChapterSelectScreen(game));
    }

    private void openDaily() {
        game.setScreen(new CharacterSelectScreen(game, () -> {
            game.session = RunSession.daily(game.meta.createLoadout(), DailySeed.today());
            game.setScreen(new BriefScreen(game));
        }));
    }

    private void openEndless() {
        if (!game.meta.endlessUnlocked) {
            return;
        }
        game.setScreen(new CharacterSelectScreen(game, () -> {
            game.session = RunSession.endless(game.meta.createLoadout(), System.nanoTime());
            game.setScreen(new BriefScreen(game));
        }));
    }

    @Override
    protected void handleShortcuts() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            game.audio.toggleMute();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            openCampaign();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            openDaily();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            openEndless();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            game.setScreen(new MetaScreen(game));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
            game.setScreen(new CodexScreen(game));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) {
            game.setScreen(new CharacterSelectScreen(game, () -> game.setScreen(new MainMenuScreen(game))));
        }
    }
}
