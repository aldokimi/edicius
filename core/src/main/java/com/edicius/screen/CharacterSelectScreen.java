package com.edicius.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
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
import com.edicius.render.ShellPreview;
import com.edicius.ui.MenuStageScreen;

import java.util.List;

public class CharacterSelectScreen extends MenuStageScreen {

    private final Runnable onConfirm;
    private int index;
    private ShellPreview preview;
    private Image previewImage;
    private TextureRegionDrawable previewDrawable;

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
    public void show() {
        preview = new ShellPreview(game.assets);
        previewDrawable = new TextureRegionDrawable(preview.region());
        previewImage = new Image(previewDrawable);
        super.show();
    }

    @Override
    protected void buildUi(Table root) {
        root.pad(28, 48, 28, 48).top().left();

        Table left = new Table();
        left.add(new Label("SELECT PROTOCOL SHELL", game.ui.skin, "heading")).left().padBottom(8).row();
        left.add(new Label("Click a shell · chroma · Confirm", game.ui.skin, "muted"))
                .left().padBottom(16).row();

        Table cards = new Table();
        List<CharacterShell> shells = CharacterCatalog.SHELLS;
        for (int i = 0; i < shells.size(); i++) {
            CharacterShell shell = shells.get(i);
            TextButton card = new TextButton(shell.displayName, game.ui.skin);
            if (i == index) {
                card.setColor(game.meta.chroma.tint);
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
            cards.add(card).width(140).height(48).padRight(8).padBottom(8);
            if ((i + 1) % 3 == 0) {
                cards.row();
            }
        }
        left.add(cards).left().padBottom(18).row();

        CharacterShell selected = shells.get(index);
        Label detailName = new Label(selected.displayName, game.ui.skin, "heading");
        detailName.setColor(game.meta.chroma.tint);
        Label detailBlurb = new Label(selected.blurb, game.ui.skin, "default");
        detailBlurb.setWrap(true);
        Label chromaLabel = new Label("Chroma: " + game.meta.chroma.label + "  (cosmetics only)", game.ui.skin, "accent");

        left.add(detailName).left().padBottom(6).row();
        left.add(detailBlurb).width(560).left().padBottom(10).row();
        left.add(chromaLabel).left().padBottom(16).row();

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
        actions.add(prevChroma).width(150).height(44).padRight(10);
        actions.add(nextChroma).width(150).height(44).padRight(18);
        actions.add(confirm).width(170).height(48).padRight(10);
        actions.add(back).width(130).height(44);
        left.add(actions).left().row();

        root.add(left).expandY().top().left().padRight(24);
        if (previewImage != null) {
            root.add(previewImage).size(380, 380).top().right();
        }
    }

    @Override
    public void render(float delta) {
        CharacterShell selected = CharacterCatalog.SHELLS.get(index);
        if (preview != null) {
            preview.setShell(selected.id, game.meta.chroma.tint);
            preview.update(delta);
            if (previewDrawable != null) {
                previewDrawable.setRegion(preview.region());
            }
        }
        super.render(delta);
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

    @Override
    public void hide() {
        super.hide();
        if (preview != null) {
            preview.dispose();
            preview = null;
        }
    }

    @Override
    public void dispose() {
        if (preview != null) {
            preview.dispose();
            preview = null;
        }
        super.dispose();
    }
}
