package com.edicius.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.edicius.gfx.BiomePalette;
import com.edicius.gfx.ChromaPreset;
import com.edicius.gfx.GameAssets;
import com.edicius.gfx.UnitSkin;
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

    public WorldRenderer(GameAssets assets) {
        this.assets = assets;
    }

    public void render(
            SpriteBatch batch,
            ShapeRenderer shapes,
            OrthographicCamera camera,
            World world,
            float dt,
            String characterId,
            ChromaPreset chroma,
            BiomePalette biome
    ) {
        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        for (Telegraph t : world.getTelegraphs()) {
            float a = 0.18f + 0.45f * t.progress();
            shapes.setColor(1f, 0.2f, 0.15f, a);
            shapes.circle(t.x, t.y, t.radius);
        }
        if (world.getRoom().type == ArenaType.HACK) {
            shapes.setColor(biome.accentTint.r, biome.accentTint.g, biome.accentTint.b, 0.35f);
            shapes.circle(world.getObjectiveX(), world.getObjectiveY(), 55f);
        }
        shapes.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawMap(batch, world, biome);
        drawEnemies(batch, world);
        drawPlayer(batch, world.getPlayer(), characterId, chroma);
        drawProjectiles(batch, world);
        drawVfx(batch, world, chroma);
        batch.end();
    }

    private void drawMap(SpriteBatch batch, World world, BiomePalette biome) {
        TileMap map = world.getMap();
        for (int x = 0; x < map.width(); x++) {
            for (int y = 0; y < map.height(); y++) {
                boolean solid = map.isSolid(x, y);
                Texture tile;
                if (solid) {
                    tile = ((x + y) % 3 == 0) ? assets.wallB : assets.wallA;
                    batch.setColor(biome.wallTint);
                } else {
                    tile = ((x * 3 + y) % 5 == 0) ? assets.floorB : assets.floorA;
                    batch.setColor(biome.floorTint);
                }
                batch.draw(tile, x * TileMap.TILE_SIZE, y * TileMap.TILE_SIZE, TileMap.TILE_SIZE, TileMap.TILE_SIZE);
                if (!solid && (x + y * 2) % 17 == 0) {
                    batch.setColor(biome.accentTint.r, biome.accentTint.g, biome.accentTint.b, 0.55f);
                    batch.draw(assets.accentTile, x * TileMap.TILE_SIZE + 16, y * TileMap.TILE_SIZE + 16, 32, 32);
                }
            }
        }
        batch.setColor(Color.WHITE);
    }

    private void drawPlayer(SpriteBatch batch, Player player, String characterId, ChromaPreset chroma) {
        UnitSkin skin = assets.playerSkin(characterId);
        Texture tex = player.moving || player.weapon != null ? skin.armed : skin.stand;
        batch.setColor(chroma.tint);
        drawRotated(batch, tex, player.x, player.y, player.aimAngleDeg - 90f, 56f * (player.isInvulnerable() ? 1.08f : 1f));
        batch.setColor(chroma.tint.r, chroma.tint.g, chroma.tint.b, 0.35f);
        batch.draw(assets.vfxFlare, player.x - 18, player.y - 18, 36, 36);
        batch.setColor(Color.WHITE);
    }

    private void drawEnemies(SpriteBatch batch, World world) {
        for (Enemy enemy : world.getEnemies()) {
            String key = enemyKey(enemy.role);
            UnitSkin skin = assets.enemySkin(key);
            Texture tex = enemy.moving ? skin.armed : skin.stand;
            batch.setColor(roleTint(enemy.role));
            float size = 52f * enemy.scale;
            drawRotated(batch, tex, enemy.x, enemy.y, enemy.aimAngleDeg - 90f, size);
            batch.setColor(Color.WHITE);
            if (enemy.huntMarked) {
                batch.setColor(1f, 1f, 0.3f, 0.85f);
                batch.draw(assets.vfxStar, enemy.x - 14, enemy.y + size * 0.35f, 28, 28);
                batch.setColor(Color.WHITE);
            }
        }
    }

    private void drawProjectiles(SpriteBatch batch, World world) {
        for (Projectile shot : world.getProjectiles()) {
            if (shot.owner == Projectile.Owner.PLAYER) {
                batch.setColor(1f, 0.95f, 0.35f, 1f);
                batch.draw(assets.vfxMuzzle, shot.x - 12, shot.y - 12, 24, 24);
            } else {
                batch.setColor(1f, 0.25f, 0.45f, 1f);
                batch.draw(assets.vfxMagic, shot.x - 14, shot.y - 14, 28, 28);
            }
            batch.setColor(Color.WHITE);
        }
    }

    private void drawVfx(SpriteBatch batch, World world, ChromaPreset chroma) {
        for (VfxBurst b : world.getBursts()) {
            float p = Math.max(0.05f, b.life / b.maxLife);
            float size = 40f * (1.4f - p);
            batch.setColor(b.r, b.g, b.b, p);
            Texture tex = b.r > 0.8f && b.g < 0.5f ? assets.vfxSpark : assets.vfxStar;
            if (b.g > 0.8f) {
                tex = assets.vfxCircle;
                batch.setColor(chroma.tint.r, chroma.tint.g, chroma.tint.b, p);
            }
            batch.draw(tex, b.x - size * 0.5f, b.y - size * 0.5f, size, size);
        }
        batch.setColor(Color.WHITE);
    }

    private static void drawRotated(SpriteBatch batch, Texture tex, float x, float y, float angleDeg, float size) {
        if (tex == null) {
            return;
        }
        float origin = size * 0.5f;
        batch.draw(
                tex,
                x - origin,
                y - origin,
                origin,
                origin,
                size,
                size,
                1f,
                1f,
                angleDeg,
                0,
                0,
                tex.getWidth(),
                tex.getHeight(),
                false,
                false
        );
    }

    private static String enemyKey(EnemyRole role) {
        return switch (role) {
            case SHOOTER, HUNT_TARGET -> "shooter";
            case DRONE, PATCHER -> "drone";
            case WARDEN, ECHO, GRID_ROOT, SHIELDER -> "boss";
            default -> "grunt";
        };
    }

    private static Color roleTint(EnemyRole role) {
        return switch (role) {
            case SHOOTER -> new Color(1f, 0.65f, 0.95f, 1f);
            case SHIELDER -> new Color(0.55f, 0.75f, 1f, 1f);
            case DRONE -> new Color(0.55f, 1f, 0.55f, 1f);
            case PATCHER -> new Color(0.8f, 0.55f, 1f, 1f);
            case HUNT_TARGET -> new Color(1f, 0.95f, 0.4f, 1f);
            case WARDEN, ECHO, GRID_ROOT -> new Color(1f, 0.45f, 0.4f, 1f);
            default -> new Color(1f, 0.7f, 0.45f, 1f);
        };
    }
}
