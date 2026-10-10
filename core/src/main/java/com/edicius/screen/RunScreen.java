package com.edicius.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.edicius.EdiciusGame;
import com.edicius.gfx.BiomePalette;
import com.edicius.input.GameInput;
import com.edicius.input.InputCommand;
import com.edicius.render.BloomEffect;
import com.edicius.render.HudRenderer;
import com.edicius.render.WorldRenderer;
import com.edicius.run.RunSession;
import com.edicius.sim.Player;
import com.edicius.sim.SimClock;
import com.edicius.sim.World;
import com.edicius.ui.UiTheme;

public class RunScreen extends ScreenAdapter {

    private static final float VIEW_W = UiTheme.VIEW_W;
    private static final float VIEW_H = UiTheme.VIEW_H;

    private final EdiciusGame game;
    private final PerspectiveCamera worldCamera = new PerspectiveCamera(34f, VIEW_W, VIEW_H);
    private final OrthographicCamera hudCamera = new OrthographicCamera();
    private final Viewport worldViewport = new FitViewport(VIEW_W, VIEW_H, worldCamera);
    private final Viewport hudViewport = new FitViewport(VIEW_W, VIEW_H, hudCamera);
    private final ShapeRenderer shapes = new ShapeRenderer();
    private final GameInput input = new GameInput();
    private final SimClock clock = new SimClock();
    private final HudRenderer hud = new HudRenderer();
    private final BloomEffect bloom = new BloomEffect();
    private final Vector3 camDesired = new Vector3();
    private final Vector3 lookDesired = new Vector3();
    private final Stage overlayStage;
    private final Table overlayRoot;
    private WorldRenderer worldRenderer;
    private World world;
    private static final float CLEAR_AUTO_ADVANCE = 1.15f;
    private static final float CLEAR_INPUT_DELAY = 0.4f;

    private float tipTimer = 12f;
    private boolean showTips;
    private boolean overlayBuilt;
    private boolean cameraSnapped;
    private float clearElapsed;
    private boolean clearAdvanced;

    public RunScreen(EdiciusGame game) {
        this.game = game;
        this.overlayStage = new Stage(hudViewport, game.batch);
        this.overlayRoot = new Table();
        overlayRoot.setFillParent(true);
        // Space is dash in-run — never let it activate overlay buttons.
        overlayStage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                return keycode == Input.Keys.SPACE;
            }
        });
        overlayStage.addActor(overlayRoot);
    }

    @Override
    public void show() {
        long seed = game.session.seed ^ (game.session.roomIndex * 31L) ^ (game.session.endlessDepth * 17L);
        world = World.forRoom(game.session.currentRoom(), game.session.loadout, game.session.activeMutator, seed);
        worldRenderer = new WorldRenderer(game.assets);
        hudCamera.setToOrtho(false, VIEW_W, VIEW_H);
        worldCamera.near = 8f;
        worldCamera.far = 4500f;
        worldCamera.viewportWidth = VIEW_W;
        worldCamera.viewportHeight = VIEW_H;
        showTips = !game.meta.tutorialSeen && game.session.roomIndex == 0
                && game.session.mode == RunSession.Mode.CAMPAIGN
                && game.session.chapter != null && "boot".equals(game.session.chapter.id);
        tipTimer = 12f;
        overlayBuilt = false;
        cameraSnapped = false;
        clearElapsed = 0f;
        clearAdvanced = false;
        centerCamera(1f);
        game.audio.startMusic();
        refreshOverlayInput();
    }

    @Override
    public void render(float delta) {
        boolean blockingUi = showTips || world.isPaused() || world.isCleared();
        if (blockingUi) {
            rebuildOverlayIfNeeded();
            overlayStage.act(delta);
        } else if (overlayBuilt) {
            overlayRoot.clear();
            overlayBuilt = false;
            refreshOverlayInput();
        }

        // Camera must be current before pick-ray aim / camera-relative move.
        centerCamera(Math.min(1f, delta * 6f));
        worldCamera.update();
        hudCamera.update();
        worldViewport.apply();

        clock.accumulate(Math.min(delta, 0.25f));
        InputCommand cmd = blockingUi
                ? InputCommand.idle(world.getPlayer().x, world.getPlayer().y)
                : input.poll(worldCamera);
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
                dismissTips();
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
        if (world.isCleared() && !clearAdvanced) {
            clearElapsed += delta;
            overlayStage.setKeyboardFocus(null);
            boolean finalRoom = !game.session.hasMoreRooms()
                    && game.session.mode != RunSession.Mode.ENDLESS;
            // Mid-run: auto-continue to upgrades so clearing a room never looks like "game over".
            if (!finalRoom && clearElapsed >= CLEAR_AUTO_ADVANCE) {
                advanceAfterClear();
                return;
            }
            if (finalRoom && clearElapsed >= CLEAR_INPUT_DELAY
                    && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                advanceAfterClear();
                return;
            }
        }

        String chapterId = game.session.chapter != null ? game.session.chapter.id : null;
        bloom.begin((int) VIEW_W, (int) VIEW_H);
        worldViewport.apply();
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
        bloom.endAndDraw(game.batch, hudCamera);

        hudViewport.apply();
        shapes.setProjectionMatrix(hudCamera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        hud.renderShapes(shapes, world, VIEW_W, VIEW_H);
        shapes.end();

        game.batch.setProjectionMatrix(hudCamera.combined);
        game.batch.begin();
        hud.renderText(game.batch, game.font, world, VIEW_W, VIEW_H, game.audio.isMuted());
        game.batch.end();

        if (blockingUi) {
            overlayStage.draw();
        }
    }

    private void rebuildOverlayIfNeeded() {
        if (overlayBuilt) {
            return;
        }
        overlayRoot.clear();
        if (showTips) {
            Table panel = new Table(game.ui.skin);
            panel.setBackground("panel");
            panel.pad(28);
            panel.add(new Label("NYX // QUICKSTART", game.ui.skin, "heading")).padBottom(12).row();
            panel.add(new Label("WASD move  ·  mouse aim  ·  LMB fire", game.ui.skin)).padBottom(6).row();
            panel.add(new Label("SPACE dash (i-frames)  ·  1/2/3 weapons", game.ui.skin)).padBottom(6).row();
            panel.add(new Label("Clear rooms, pick upgrades, delete Warden-01", game.ui.skin)).padBottom(16).row();
            TextButton dismiss = new TextButton("Got it", game.ui.skin);
            dismiss.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    dismissTips();
                }
            });
            panel.add(dismiss).width(200).height(46);
            overlayRoot.add(panel).center();
        } else if (world.isPaused()) {
            Table panel = new Table(game.ui.skin);
            panel.setBackground("panel");
            panel.pad(28);
            panel.add(new Label("PAUSED", game.ui.skin, "heading")).padBottom(16).row();
            TextButton resume = new TextButton("Resume", game.ui.skin);
            resume.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    // toggle pause via a fake escape next tick
                    world.update(new InputCommand(0, 0, world.getPlayer().x, world.getPlayer().y,
                            false, false, true, false, false, false, false), 0f);
                    overlayBuilt = false;
                    refreshOverlayInput();
                }
            });
            panel.add(resume).width(200).height(46);
            overlayRoot.add(panel).center();
        } else if (world.isCleared()) {
            Table panel = new Table(game.ui.skin);
            panel.setBackground("panel");
            panel.pad(28);
            boolean finalRoom = !game.session.hasMoreRooms()
                    && game.session.mode != RunSession.Mode.ENDLESS;
            String title = finalRoom ? "SECTOR CLEAR" : "ROOM CLEAR";
            panel.add(new Label(title, game.ui.skin, "heading")).padBottom(10).row();
            panel.add(new Label(clearProgressLabel(finalRoom), game.ui.skin, "muted")).padBottom(16).row();
            if (finalRoom) {
                TextButton cont = new TextButton("Raid report", game.ui.skin);
                cont.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (clearElapsed >= CLEAR_INPUT_DELAY) {
                            advanceAfterClear();
                        }
                    }
                });
                panel.add(cont).width(220).height(48);
            } else {
                panel.add(new Label("Loading upgrade bay…", game.ui.skin, "accent"));
            }
            overlayRoot.add(panel).center();
        }
        overlayBuilt = true;
        refreshOverlayInput();
    }

    private void dismissTips() {
        showTips = false;
        game.meta.markTutorialSeen();
        overlayBuilt = false;
        refreshOverlayInput();
    }

    private String clearProgressLabel(boolean finalRoom) {
        if (game.session.mode == RunSession.Mode.ENDLESS) {
            return "Depth " + (game.session.endlessDepth + 1) + " secured — next wave incoming";
        }
        if (game.session.chapter == null) {
            return finalRoom ? "Enter for raid report" : "Next: protocol upgrade";
        }
        int room = game.session.roomIndex + 1;
        int total = game.session.chapter.rooms.size();
        if (finalRoom) {
            return "Room " + room + " / " + total + " — sector secured";
        }
        return "Room " + room + " / " + total + " — next: choose an upgrade";
    }

    private void advanceAfterClear() {
        if (clearAdvanced) {
            return;
        }
        clearAdvanced = true;
        game.session.loadout.syncFromPlayer(world.getPlayer());
        if (game.session.hasMoreRooms() || game.session.mode == RunSession.Mode.ENDLESS) {
            game.setScreen(new UpgradeScreen(game));
        } else {
            game.session.advanceRoom();
            game.session.victory = true;
            game.setScreen(new ResultsScreen(game));
        }
    }

    private void refreshOverlayInput() {
        boolean blockingUi = showTips || (world != null && (world.isPaused() || world.isCleared()));
        if (blockingUi) {
            Gdx.input.setInputProcessor(overlayStage);
        } else {
            Gdx.input.setInputProcessor(null);
        }
    }

    private void playEvents() {
        Player p = world.getPlayer();
        if (p.eventDash) {
            game.audio.playDash();
            worldRenderer.particles().dash(p.x, p.y, p.aimAngleDeg > -999 ? (float) Math.cos(Math.toRadians(p.aimAngleDeg)) * 200f : 200f,
                    (float) Math.sin(Math.toRadians(p.aimAngleDeg)) * 200f);
        }
        if (p.eventShoot) {
            game.audio.playShoot();
            worldRenderer.particles().muzzle(p.x, p.y, p.aimAngleDeg);
        }
        if (p.eventHurt || world.eventHitConfirm) {
            game.audio.playHit();
            if (world.eventHitConfirm) {
                // approximate last hit near player aim
                worldRenderer.particles().hit(p.x + (float) Math.cos(Math.toRadians(p.aimAngleDeg)) * 40f,
                        p.y + (float) Math.sin(Math.toRadians(p.aimAngleDeg)) * 40f);
            }
        }
        if (world.eventRoomClear) {
            game.audio.playRoomClear();
            worldRenderer.particles().roomClear(p.x, p.y);
            overlayBuilt = false;
        }
        if (world.isPaused()) {
            overlayBuilt = false;
        }
    }

    private void centerCamera(float blend) {
        Player player = world.getPlayer();
        // Isometric-style chase: 45° yaw, ~34° pitch (sim x/y → world x/z)
        float dist = 720f;
        float yawRad = (float) Math.toRadians(45f);
        float pitchRad = (float) Math.toRadians(34f);
        float horiz = dist * (float) Math.cos(pitchRad);
        float height = dist * (float) Math.sin(pitchRad);
        camDesired.set(
                player.x + horiz * (float) Math.sin(yawRad),
                height,
                player.y + horiz * (float) Math.cos(yawRad));
        lookDesired.set(player.x, 22f, player.y);
        worldCamera.fieldOfView = 34f;
        if (!cameraSnapped || blend >= 1f) {
            worldCamera.position.set(camDesired);
            cameraSnapped = true;
        } else {
            worldCamera.position.lerp(camDesired, blend);
        }
        worldCamera.lookAt(lookDesired.x, lookDesired.y, lookDesired.z);
        worldCamera.up.set(0f, 1f, 0f);
        worldCamera.update();
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height, true);
        hudViewport.update(width, height, true);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        shapes.dispose();
        bloom.dispose();
        overlayStage.dispose();
        if (worldRenderer != null) {
            worldRenderer.dispose();
        }
    }
}
