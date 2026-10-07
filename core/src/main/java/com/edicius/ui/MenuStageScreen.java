package com.edicius.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.edicius.EdiciusGame;

/** Scene2D menu base with neon theme, background, and input wiring. */
public abstract class MenuStageScreen extends ScreenAdapter {

    protected final EdiciusGame game;
    protected final OrthographicCamera camera = new OrthographicCamera();
    protected final Viewport viewport = new FitViewport(UiTheme.VIEW_W, UiTheme.VIEW_H, camera);
    protected final Stage stage;
    protected final Table root;

    protected MenuStageScreen(EdiciusGame game) {
        this.game = game;
        this.stage = new Stage(viewport, game.batch);
        this.root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        root.clear();
        buildUi(root);
    }

    protected abstract void buildUi(Table root);

    protected void drawBackground(float r, float g, float b) {
        Gdx.gl.glClearColor(r, g, b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (game.assets.menuBg != null) {
            game.batch.setProjectionMatrix(camera.combined);
            game.batch.begin();
            game.batch.setColor(1f, 1f, 1f, 0.45f);
            game.batch.draw(game.assets.menuBg, 0, 0, UiTheme.VIEW_W, UiTheme.VIEW_H);
            game.batch.setColor(Color.WHITE);
            game.batch.end();
        }
    }

    @Override
    public void render(float delta) {
        drawBackground(0.03f, 0.04f, 0.09f);
        handleShortcuts();
        stage.act(delta);
        stage.draw();
    }

    /** Optional keyboard duplicates; subclasses override. */
    protected void handleShortcuts() {
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void hide() {
        if (Gdx.input.getInputProcessor() == stage) {
            Gdx.input.setInputProcessor(null);
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
