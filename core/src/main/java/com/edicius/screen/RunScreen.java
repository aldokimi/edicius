package com.edicius.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.edicius.EdiciusGame;
import com.edicius.input.GameInput;
import com.edicius.input.InputCommand;
import com.edicius.gfx.BiomePalette;
import com.edicius.render.HudRenderer;
import com.edicius.render.WorldRenderer;
import com.edicius.sim.Player;
import com.edicius.sim.SimClock;
import com.edicius.sim.World;

public class RunScreen extends ScreenAdapter {

    private static final float VIEW_W = 1280f;
    private static final float VIEW_H = 720f;

    private final EdiciusGame game;
    private final OrthographicCamera worldCamera = new OrthographicCamera();
    private final OrthographicCamera hudCamera = new OrthographicCamera();
    private final Viewport worldViewport = new FitViewport(VIEW_W, VIEW_H, worldCamera);
    private final Viewport hudViewport = new FitViewport(VIEW_W, VIEW_H, hudCamera);
    private final ShapeRenderer shapes = new ShapeRenderer();
    private final GameInput input = new GameInput();
    private final SimClock clock = new SimClock();
    private final HudRenderer hud = new HudRenderer();
    private WorldRenderer worldRenderer;
    private World world;
    private float tipTimer = 12f;
    private boolean showTips;

    public RunScreen(EdiciusGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        long seed = game.session.seed ^ (game.session.roomIndex * 31L) ^ (game.session.endlessDepth * 17L);
        world = World.forRoom(game.session.currentRoom(), game.session.loadout, game.session.activeMutator, seed);
        worldRenderer = new WorldRenderer(game.assets);
        hudCamera.setToOrtho(false, VIEW_W, VIEW_H);
        worldCamera.setToOrtho(false, VIEW_W, VIEW_H);
        showTips = !game.meta.tutorialSeen && game.session.roomIndex == 0
                && game.session.mode == com.edicius.run.RunSession.Mode.CAMPAIGN
                && game.session.chapter != null && "boot".equals(game.session.chapter.id);
        tipTimer = 12f;
        centerCamera();
        game.audio.startMusic();
    }

    @Override
    public void render(float delta) {
        clock.accumulate(Math.min(delta, 0.25f));
        InputCommand cmd = input.poll(worldCamera);
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            game.audio.toggleMute();
        }
        while (clock.consumeTick()) {
            world.update(cmd, SimClock.TICK);
            playEvents();
            cmd = new InputCommand(cmd.moveX(), cmd.moveY(), cmd.aimWorldX(), cmd.aimWorldY(),
                    cmd.fire(), false, false, false, false, false, false);
        }

        if (showTips) {
            tipTimer -= delta;
            if (tipTimer <= 0f || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                showTips = false;
                game.meta.markTutorialSeen();
            }
        }

        if (world.isPlayerDead()) {
            game.session.loadout.syncFromPlayer(world.getPlayer());
            game.session.failed = true;
            game.meta.deathLog.record(world.getRoom().id);
            game.meta.save();
            game.setScreen(new ResultsScreen(game));
            return;
        }
        if (world.isCleared() && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.session.loadout.syncFromPlayer(world.getPlayer());
            if (game.session.hasMoreRooms()) {
                game.setScreen(new UpgradeScreen(game));
            } else if (game.session.mode != com.edicius.run.RunSession.Mode.ENDLESS) {
                game.session.victory = true;
                game.setScreen(new ResultsScreen(game));
            } else {
                game.setScreen(new UpgradeScreen(game));
            }
            return;
        }

        centerCamera();
        worldCamera.update();
        hudCamera.update();

        Gdx.gl.glClearColor(0.02f, 0.02f, 0.06f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        worldViewport.apply();
        String chapterId = game.session.chapter != null ? game.session.chapter.id : null;
        worldRenderer.render(
                game.batch,
                shapes,
                worldCamera,
                world,
                delta,
                game.meta.selectedCharacterId,
                game.meta.chroma,
                BiomePalette.forChapter(chapterId)
        );

        hudViewport.apply();
        shapes.setProjectionMatrix(hudCamera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        hud.renderShapes(shapes, world, VIEW_W, VIEW_H);
        shapes.end();

        game.batch.setProjectionMatrix(hudCamera.combined);
        game.batch.begin();
        hud.renderText(game.batch, game.font, world, VIEW_W, VIEW_H, game.audio.isMuted());
        if (showTips) {
            hud.renderTutorial(game.batch, game.font, VIEW_W, VIEW_H);
        }
        game.batch.end();
    }

    private void playEvents() {
        Player p = world.getPlayer();
        if (p.eventDash) game.audio.playDash();
        if (p.eventShoot) game.audio.playShoot();
        if (p.eventHurt || world.eventHitConfirm) game.audio.playHit();
        if (world.eventRoomClear) game.audio.playRoomClear();
    }

    private void centerCamera() {
        Player player = world.getPlayer();
        float halfW = VIEW_W * 0.5f;
        float halfH = VIEW_H * 0.5f;
        float camX = Math.max(halfW, Math.min(player.x, world.getMap().worldWidth() - halfW));
        float camY = Math.max(halfH, Math.min(player.y, world.getMap().worldHeight() - halfH));
        worldCamera.position.set(camX, camY, 0f);
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height, true);
        hudViewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        shapes.dispose();
    }
}
