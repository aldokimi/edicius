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
import com.edicius.render.HudRenderer;
import com.edicius.sim.Enemy;
import com.edicius.sim.EnemyRole;
import com.edicius.sim.Player;
import com.edicius.sim.Projectile;
import com.edicius.sim.SimClock;
import com.edicius.sim.TileMap;
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
    private World world;

    public RunScreen(EdiciusGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        world = World.loadDefault();
        hudCamera.setToOrtho(false, VIEW_W, VIEW_H);
        worldCamera.setToOrtho(false, VIEW_W, VIEW_H);
        centerCamera();
    }

    @Override
    public void render(float delta) {
        clock.accumulate(Math.min(delta, 0.25f));
        InputCommand cmd = input.poll(worldCamera);
        while (clock.consumeTick()) {
            world.update(cmd, SimClock.TICK);
            cmd = new InputCommand(cmd.moveX(), cmd.moveY(), cmd.aimWorldX(), cmd.aimWorldY(),
                    cmd.fire(), false, false);
        }

        if (world.isPlayerDead()) {
            game.setScreen(new MainMenuScreen(game, "Total kills: " + world.getPlayer().kills));
            return;
        }
        if (world.isCleared() && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new MainMenuScreen(game, "Sector clear — kills: " + world.getPlayer().kills));
            return;
        }

        centerCamera();
        worldCamera.update();
        hudCamera.update();

        Gdx.gl.glClearColor(0.02f, 0.02f, 0.06f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        worldViewport.apply();
        shapes.setProjectionMatrix(worldCamera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        drawMap();
        drawEntities();
        shapes.end();

        hudViewport.apply();
        shapes.setProjectionMatrix(hudCamera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        hud.renderShapes(shapes, world, VIEW_W, VIEW_H);
        shapes.end();

        game.batch.setProjectionMatrix(hudCamera.combined);
        game.batch.begin();
        hud.renderText(game.batch, game.font, world, VIEW_W, VIEW_H);
        game.batch.end();
    }

    private void drawMap() {
        TileMap map = world.getMap();
        for (int x = 0; x < map.width(); x++) {
            for (int y = 0; y < map.height(); y++) {
                if (map.isSolid(x, y)) {
                    shapes.setColor(0.25f, 0.35f, 0.55f, 1f);
                } else {
                    shapes.setColor(0.08f, 0.1f, 0.16f, 1f);
                }
                shapes.rect(x * TileMap.TILE_SIZE, y * TileMap.TILE_SIZE, TileMap.TILE_SIZE, TileMap.TILE_SIZE);
            }
        }
    }

    private void drawEntities() {
        Player player = world.getPlayer();
        shapes.setColor(0.2f, 0.95f, 0.85f, 1f);
        shapes.rect(player.x - Player.HALF_W, player.y - Player.HALF_H, Player.HALF_W * 2, Player.HALF_H * 2);

        for (Enemy enemy : world.getEnemies()) {
            if (enemy.role == EnemyRole.SHOOTER) {
                shapes.setColor(0.95f, 0.35f, 0.75f, 1f);
            } else {
                shapes.setColor(0.95f, 0.45f, 0.25f, 1f);
            }
            shapes.rect(enemy.x - Enemy.HALF_W, enemy.y - Enemy.HALF_H, Enemy.HALF_W * 2, Enemy.HALF_H * 2);
        }

        for (Projectile shot : world.getProjectiles()) {
            if (shot.owner == Projectile.Owner.PLAYER) {
                shapes.setColor(1f, 0.9f, 0.2f, 1f);
            } else {
                shapes.setColor(1f, 0.2f, 0.35f, 1f);
            }
            shapes.rect(shot.x - Projectile.HALF, shot.y - Projectile.HALF, Projectile.HALF * 2, Projectile.HALF * 2);
        }
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
