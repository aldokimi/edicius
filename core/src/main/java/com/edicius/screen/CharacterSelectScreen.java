package com.edicius.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.edicius.EdiciusGame;
import com.edicius.gfx.CharacterCatalog;
import com.edicius.gfx.CharacterShell;
import com.edicius.gfx.UnitSkin;
import com.edicius.ui.MenuStageScreen;

import java.util.List;

public class CharacterSelectScreen extends MenuStageScreen {

    private final Runnable onConfirm;
    private int index;

    public CharacterSelectScreen(EdiciusGame game, Runnable onConfirm) {
        super(game);
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
    protected void buildUi(Table root) {
        root.pad(36, 60, 36, 60).top().left();
        root.add(new Label("SELECT PROTOCOL SHELL", game.ui.skin, "heading")).left().padBottom(8).colspan(2).row();
        root.add(new Label("Click a shell · chroma buttons · Confirm", game.ui.skin, "muted"))
                .left().padBottom(18).colspan(2).row();

        Table cards = new Table();
        List<CharacterShell> shells = CharacterCatalog.SHELLS;
        for (int i = 0; i < shells.size(); i++) {
            CharacterShell shell = shells.get(i);
            UnitSkin skin = game.assets.playerSkin(shell.id);
            Texture preview = skin.armed != null ? skin.armed : skin.stand;
            TextButton card = new TextButton(shell.displayName, game.ui.skin);
            if (preview != null) {
                Image img = new Image(new TextureRegionDrawable(preview));
                img.setColor(i == index ? game.meta.chroma.tint : new Color(0.6f, 0.6f, 0.65f, 1f));
                Table inner = new Table();
                inner.add(img).size(88, 88).padBottom(6).row();
                inner.add(new Label(shell.displayName, game.ui.skin, i == index ? "accent" : "muted"));
                card.clearChildren();
                card.add(inner).pad(8);
            }
            final int pick = i;
            card.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    index = pick;
                    root.clear();
                    buildUi(root);
                }
            });
            cards.add(card).size(150, 140).padRight(12);
        }
        root.add(cards).left().colspan(2).padBottom(20).row();

        CharacterShell selected = shells.get(index);
        Label detailName = new Label(selected.displayName, game.ui.skin, "heading");
        detailName.setColor(game.meta.chroma.tint);
        Label detailBlurb = new Label(selected.blurb, game.ui.skin, "default");
        detailBlurb.setWrap(true);
        Label chromaLabel = new Label("Chroma: " + game.meta.chroma.label + "  (cosmetics only)", game.ui.skin, "accent");

        root.add(detailName).left().colspan(2).padBottom(6).row();
        root.add(detailBlurb).width(900).left().colspan(2).padBottom(10).row();
        root.add(chromaLabel).left().colspan(2).padBottom(16).row();

        Table actions = new Table();
        TextButton prevChroma = new TextButton("◀ Chroma", game.ui.skin);
        prevChroma.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.meta.chroma = game.meta.chroma.prev();
                game.meta.cosmeticTrail = game.meta.chroma.name().toLowerCase();
                game.meta.save();
                root.clear();
                buildUi(root);
            }
        });
        TextButton nextChroma = new TextButton("Chroma ▶", game.ui.skin);
        nextChroma.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.meta.cycleCosmetic();
                root.clear();
                buildUi(root);
            }
        });
        TextButton confirm = new TextButton("Confirm", game.ui.skin);
        confirm.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                confirmSelection();
            }
        });
        TextButton back = new TextButton("Back", game.ui.skin, "danger");
        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        actions.add(prevChroma).width(160).height(44).padRight(10);
        actions.add(nextChroma).width(160).height(44).padRight(20);
        actions.add(confirm).width(180).height(48).padRight(10);
        actions.add(back).width(140).height(44);
        root.add(actions).left().colspan(2).row();
    }

    private void confirmSelection() {
        CharacterShell selected = CharacterCatalog.SHELLS.get(index);
        game.meta.selectCharacter(selected.id);
        onConfirm.run();
    }

    @Override
    protected void handleShortcuts() {
        List<CharacterShell> shells = CharacterCatalog.SHELLS;
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            index = (index + 1) % shells.size();
            root.clear();
            buildUi(root);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            index = (index - 1 + shells.size()) % shells.size();
            root.clear();
            buildUi(root);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.E) || Gdx.input.isKeyJustPressed(Input.Keys.PERIOD)) {
            game.meta.cycleCosmetic();
            root.clear();
            buildUi(root);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q) || Gdx.input.isKeyJustPressed(Input.Keys.COMMA)) {
            game.meta.chroma = game.meta.chroma.prev();
            game.meta.cosmeticTrail = game.meta.chroma.name().toLowerCase();
            game.meta.save();
            root.clear();
            buildUi(root);
        }
        for (int i = 0; i < shells.size(); i++) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i)) {
                index = i;
                root.clear();
                buildUi(root);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            confirmSelection();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
        }
    }
}
