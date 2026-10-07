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
import com.edicius.sim.Telegraph;
import com.edicius.sim.TileMap;
import com.edicius.sim.VfxBurst;
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
        long seed = game.session.seed ^ (game.session.roomIndex * 31L) ^ (game.session.endlessDepth * 17L);
        world = World.forRoom(game.session.currentRoom(), game.session.loadout, game.session.activeMutator, seed);
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
                    cmd.fire(), false, false, false, false, false, false);
        }

        if (world.isPlayerDead()) {
            game.session.loadout.syncFromPlayer(world.getPlayer());
            game.session.failed = true;
            game.setScreen(new ResultsScreen(game));
            return;
        }
        if (world.isCleared() && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.session.loadout.syncFromPlayer(world.getPlayer());
            if (game.session.hasMoreRooms()) {
                game.setScreen(new UpgradeScreen(game));
            } else {
                if (game.session.mode != com.edicius.run.RunSession.Mode.ENDLESS) {
                    game.session.victory = true;
                    game.setScreen(new ResultsScreen(game));
                } else {
                    game.setScreen(new UpgradeScreen(game));
                }
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
        shapes.setProjectionMatrix(worldCamera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        drawMap();
        drawObjective();
        drawTelegraphs();
        drawEntities();
        drawVfx();
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
        boolean mirror = game.session.chapter != null && "mirror".equals(game.session.chapter.id);
        boolean root = game.session.chapter != null && "root".equals(game.session.chapter.id);
        for (int x = 0; x < map.width(); x++) {
            for (int y = 0; y < map.height(); y++) {
                if (map.isSolid(x, y)) {
                    if (mirror) {
                        shapes.setColor(0.45f, 0.2f, 0.55f, 1f);
                    } else if (root) {
                        shapes.setColor(0.35f, 0.15f, 0.2f, 1f);
                    } else {
                        shapes.setColor(0.25f, 0.35f, 0.55f, 1f);
                    }
                } else {
                    shapes.setColor(0.08f, 0.1f, 0.16f, 1f);
                }
                shapes.rect(x * TileMap.TILE_SIZE, y * TileMap.TILE_SIZE, TileMap.TILE_SIZE, TileMap.TILE_SIZE);
            }
        }
    }

    private void drawObjective() {
        if (world.getRoom().type == com.edicius.sim.ArenaType.HACK) {
            shapes.setColor(0.2f, 0.8f, 1f, 0.35f);
            shapes.circle(world.getObjectiveX(), world.getObjectiveY(), 55f);
        }
    }

    private void drawTelegraphs() {
        for (Telegraph t : world.getTelegraphs()) {
            float a = 0.15f + 0.35f * t.progress();
            shapes.setColor(1f, 0.2f, 0.15f, a);
            shapes.circle(t.x, t.y, t.radius);
        }
    }

    private void drawVfx() {
        for (VfxBurst b : world.getBursts()) {
            float p = b.life / b.maxLife;
            shapes.setColor(b.r, b.g, b.b, p);
            shapes.circle(b.x, b.y, 18f * (1.2f - p));
        }
    }

    private void drawEntities() {
        Player player = world.getPlayer();
        switch (game.meta.cosmeticTrail) {
            case "magenta" -> shapes.setColor(0.95f, 0.3f, 0.85f, 1f);
            case "gold" -> shapes.setColor(1f, 0.85f, 0.25f, 1f);
            default -> shapes.setColor(0.2f, 0.95f, 0.85f, 1f);
        }
        shapes.rect(player.x - Player.HALF_W, player.y - Player.HALF_H, Player.HALF_W * 2, Player.HALF_H * 2);

        for (Enemy enemy : world.getEnemies()) {
            shapes.setColor(colorFor(enemy));
            shapes.rect(enemy.x - enemy.halfW(), enemy.y - enemy.halfH(), enemy.halfW() * 2, enemy.halfH() * 2);
            if (enemy.huntMarked) {
                shapes.setColor(1f, 1f, 0.2f, 0.5f);
                shapes.circle(enemy.x, enemy.y, 28f);
            }
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

    private com.badlogic.gdx.graphics.Color colorFor(Enemy enemy) {
        return switch (enemy.role) {
            case SHOOTER -> new com.badlogic.gdx.graphics.Color(0.95f, 0.35f, 0.75f, 1f);
            case SHIELDER -> new com.badlogic.gdx.graphics.Color(0.4f, 0.6f, 1f, 1f);
            case DRONE -> new com.badlogic.gdx.graphics.Color(0.6f, 1f, 0.4f, 1f);
            case PATCHER -> new com.badlogic.gdx.graphics.Color(0.8f, 0.5f, 1f, 1f);
            case HUNT_TARGET -> new com.badlogic.gdx.graphics.Color(1f, 0.9f, 0.2f, 1f);
            case WARDEN, ECHO, GRID_ROOT -> new com.badlogic.gdx.graphics.Color(1f, 0.25f, 0.25f, 1f);
            default -> new com.badlogic.gdx.graphics.Color(0.95f, 0.45f, 0.25f, 1f);
        };
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
