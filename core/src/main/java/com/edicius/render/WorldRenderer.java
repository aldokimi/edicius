package com.edicius.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.edicius.gfx.GameAssets;
import com.edicius.gfx.SheetAnimator;
import com.edicius.sim.ArenaType;
import com.edicius.sim.Enemy;
import com.edicius.sim.EnemyRole;
import com.edicius.sim.Player;
import com.edicius.sim.Projectile;
import com.edicius.sim.Telegraph;
import com.edicius.sim.TileMap;
import com.edicius.sim.VfxBurst;
import com.edicius.sim.World;

public class WorldRenderer {

    private final GameAssets assets;
    private final SheetAnimator playerAnim;
    private final SheetAnimator enemyAnim;

    public WorldRenderer(GameAssets assets) {
        this.assets = assets;
        this.playerAnim = new SheetAnimator(assets.daveStand, assets.daveWalk, 0.12f);
        this.enemyAnim = new SheetAnimator(assets.mattStand, assets.mattWalk, 0.14f);
    }

    public void render(SpriteBatch batch, ShapeRenderer shapes, OrthographicCamera camera, World world, float dt, String trail) {
        playerAnim.update(dt, world.getPlayer().facing, world.getPlayer().moving);

        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        for (Telegraph t : world.getTelegraphs()) {
            float a = 0.15f + 0.4f * t.progress();
            shapes.setColor(1f, 0.25f, 0.2f, a);
            shapes.circle(t.x, t.y, t.radius);
        }
        if (world.getRoom().type == ArenaType.HACK) {
            shapes.setColor(0.2f, 0.85f, 1f, 0.3f);
            shapes.circle(world.getObjectiveX(), world.getObjectiveY(), 55f);
        }
        for (VfxBurst b : world.getBursts()) {
            float p = b.life / b.maxLife;
            shapes.setColor(b.r, b.g, b.b, p);
            shapes.circle(b.x, b.y, 20f * (1.3f - p));
        }
        shapes.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        TileMap map = world.getMap();
        for (int x = 0; x < map.width(); x++) {
            for (int y = 0; y < map.height(); y++) {
                TextureRegion tile = new TextureRegion(map.isSolid(x, y) ? assets.wall : assets.floor);
                batch.draw(tile, x * TileMap.TILE_SIZE, y * TileMap.TILE_SIZE, TileMap.TILE_SIZE, TileMap.TILE_SIZE);
            }
        }

        for (Enemy enemy : world.getEnemies()) {
            enemyAnim.update(dt, enemy.facing, enemy.moving);
            TextureRegion reg = enemyAnim.region();
            float w = SheetAnimator.FRAME * enemy.scale;
            float h = SheetAnimator.FRAME * enemy.scale;
            Color tint = tintFor(enemy.role);
            batch.setColor(tint);
            batch.draw(reg, enemy.x - w * 0.5f, enemy.y - h * 0.5f, w, h);
            batch.setColor(Color.WHITE);
            if (enemy.huntMarked) {
                batch.setColor(1f, 1f, 0.2f, 0.7f);
                batch.draw(assets.shot, enemy.x - 10, enemy.y + h * 0.4f, 20, 20);
                batch.setColor(Color.WHITE);
            }
        }

        Player player = world.getPlayer();
        batch.setColor(trailColor(trail));
        TextureRegion preg = playerAnim.region();
        batch.draw(preg, player.x - 32, player.y - 32, 64, 64);
        batch.setColor(Color.WHITE);

        for (Projectile shot : world.getProjectiles()) {
            if (shot.owner == Projectile.Owner.PLAYER) {
                batch.draw(assets.shot, shot.x - 16, shot.y - 16, 32, 32);
            } else {
                batch.setColor(1f, 0.4f, 0.45f, 1f);
                batch.draw(assets.shot, shot.x - 14, shot.y - 14, 28, 28);
                batch.setColor(Color.WHITE);
            }
        }
        batch.end();
    }

    private static Color trailColor(String trail) {
        return switch (trail) {
            case "magenta" -> new Color(0.95f, 0.45f, 0.9f, 1f);
            case "gold" -> new Color(1f, 0.88f, 0.35f, 1f);
            default -> Color.WHITE;
        };
    }

    private static Color tintFor(EnemyRole role) {
        return switch (role) {
            case SHOOTER -> new Color(1f, 0.7f, 0.95f, 1f);
            case SHIELDER -> new Color(0.65f, 0.8f, 1f, 1f);
            case DRONE -> new Color(0.7f, 1f, 0.7f, 1f);
            case PATCHER -> new Color(0.85f, 0.7f, 1f, 1f);
            case HUNT_TARGET -> new Color(1f, 0.95f, 0.5f, 1f);
            case WARDEN, ECHO, GRID_ROOT -> new Color(1f, 0.55f, 0.55f, 1f);
            default -> Color.WHITE;
        };
    }
}
