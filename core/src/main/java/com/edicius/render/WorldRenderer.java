package com.edicius.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.edicius.gfx.BiomePalette;
import com.edicius.gfx.ChromaPreset;
import com.edicius.gfx.GameAssets;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;
import com.edicius.sim.ArenaType;
import com.edicius.sim.Enemy;
import com.edicius.sim.EnemyRole;
import com.edicius.sim.Player;
import com.edicius.sim.Projectile;
import com.edicius.sim.Telegraph;
import com.edicius.sim.TileMap;
import com.edicius.sim.VfxBurst;
import com.edicius.sim.World;

/**
 * Perspective 3D arena: extruded walls + Kenney Blocky GLB humanoids (PBR).
 * Sim space (x,y) maps to world (x, z) with height on Y.
 */
public class WorldRenderer implements Disposable {

    public static final float WALL_H = 56f;
    private static final float FLOOR_H = 4f;
    /** Kenney blocky characters are ~2m tall in model space. */
    private static final float UNIT_SCALE = 30f;
    /** glTF +Z forward → sim +X (aim 0°) needs −90° yaw. */
    private static final float MODEL_YAW_OFFSET = -90f;

    private final GameAssets assets;
    private final ParticleBus particles = new ParticleBus();
    /** Classic materials for extruded floor/wall boxes. */
    private final ModelBatch worldBatch = new ModelBatch();
    /** PBR materials for glTF/GLB characters and props. */
    private final ModelBatch unitBatch;
    private final Environment environment = new Environment();
    private final ModelBuilder modelBuilder = new ModelBuilder();
    private final Array<ModelInstance> solids = new Array<>();
    /** PBR glTF props — must not share the classic ModelBatch with floor/wall boxes. */
    private final Array<ModelInstance> props = new Array<>();
    private final Array<Model> ownedModels = new Array<>();
    private final Matrix4 shapeXform = new Matrix4();
    private final PointLight playerLight = new PointLight();
    private float animTime;

    private DecalBatch decalBatch;
    private CameraGroupStrategy decalStrategy;
    private TileMap bakedMap;
    private ModelInstance[][] floorInst;
    private ModelInstance[][] wallInst;

    public WorldRenderer(GameAssets assets) {
        this.assets = assets;
        this.unitBatch = new ModelBatch(new PBRShaderProvider(PBRShaderProvider.createDefaultConfig()));
        // Bright ambient so PBR characters read clearly without IBL
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.55f, 0.58f, 0.65f, 1f));
        environment.add(new DirectionalLight().set(1f, 1f, 1f, -0.35f, -1f, -0.25f));
        environment.add(new DirectionalLight().set(0.55f, 0.45f, 0.75f, 0.45f, -0.15f, 0.55f));
        environment.add(playerLight);
    }

    public ParticleBus particles() {
        return particles;
    }

    public void render(
            SpriteBatch batch,
            ShapeRenderer shapes,
            Camera camera,
            World world,
            float dt,
            String characterId,
            ChromaPreset chroma,
            BiomePalette biome
    ) {
        animTime += dt;
        particles.update(dt);
        ensureDecals(camera);
        bakeIfNeeded(world.getMap(), biome);

        Player player = world.getPlayer();
        playerLight.set(chroma.tint.r, chroma.tint.g, chroma.tint.b, player.x, 55f, player.y, 320f);

        worldBatch.begin(camera);
        for (ModelInstance m : solids) {
            worldBatch.render(m, environment);
        }
        worldBatch.end();

        unitBatch.begin(camera);
        for (ModelInstance m : props) {
            unitBatch.render(m, environment);
        }
        drawUnitMeshes(world, characterId, chroma);
        unitBatch.end();

        // Ground markers (telegraphs / hack) as discs on the XZ floor plane
        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        for (Telegraph t : world.getTelegraphs()) {
            float a = 0.22f + 0.5f * t.progress();
            shapes.setColor(1f, 0.15f, 0.2f, a);
            shapeXform.idt().translate(t.x, 2f, t.y).rotate(Vector3.X, 90f);
            shapes.setTransformMatrix(shapeXform);
            shapes.circle(0f, 0f, t.radius, 28);
        }
        if (world.getRoom().type == ArenaType.HACK) {
            shapes.setColor(biome.accentTint.r, biome.accentTint.g, biome.accentTint.b, 0.4f);
            shapeXform.idt().translate(world.getObjectiveX(), 2f, world.getObjectiveY()).rotate(Vector3.X, 90f);
            shapes.setTransformMatrix(shapeXform);
            shapes.circle(0f, 0f, 55f, 28);
        }
        shapes.identity();
        shapes.end();

        ensureDecalBatch(camera);
        for (Enemy enemy : world.getEnemies()) {
            if (enemy.huntMarked) {
                addBillboard(assets.vfxStar, enemy.x, 48f * enemy.scale, enemy.y, 28f, Color.YELLOW, 0f);
            }
        }
        drawProjectileDecals(world);
        drawBurstDecals(world, chroma);
        particles.drawAsDecals(decalBatch, assets, decalStrategy.getCamera().position);
        decalBatch.flush();
    }

    private void ensureDecals(Camera camera) {
        if (decalStrategy == null || decalStrategy.getCamera() != camera) {
            if (decalBatch != null) {
                decalBatch.dispose();
            }
            if (decalStrategy != null) {
                decalStrategy.dispose();
            }
            decalStrategy = new CameraGroupStrategy(camera);
            decalBatch = new DecalBatch(decalStrategy);
        }
    }

    private DecalBatch ensureDecalBatch(Camera camera) {
        ensureDecals(camera);
        return decalBatch;
    }

    private void bakeIfNeeded(TileMap map, BiomePalette biome) {
        if (bakedMap == map && floorInst != null) {
            return;
        }
        clearBake();
        bakedMap = map;
        int w = map.width();
        int h = map.height();
        floorInst = new ModelInstance[w][h];
        wallInst = new ModelInstance[w][h];
        float ts = TileMap.TILE_SIZE;

        Model floorModelA = box(ts, FLOOR_H, ts, assets.floorA, biome.floorTint);
        Model floorModelB = box(ts, FLOOR_H, ts, assets.floorB, biome.floorTint);
        Model wallModelA = box(ts * 0.95f, WALL_H, ts * 0.95f, assets.wallA, biome.wallTint);
        Model wallModelB = box(ts * 0.95f, WALL_H, ts * 0.95f, assets.wallB, biome.wallTint);
        ownedModels.add(floorModelA);
        ownedModels.add(floorModelB);
        ownedModels.add(wallModelA);
        ownedModels.add(wallModelB);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                float cx = map.tileCenterX(x);
                float cz = map.tileCenterY(y);
                boolean solid = map.isSolid(x, y);
                Model floorM = map.isAccentFloor(x, y) || ((x * 3 + y) % 5 == 0)
                        ? floorModelB : floorModelA;
                ModelInstance floor = new ModelInstance(floorM);
                floor.transform.setToTranslation(cx, FLOOR_H * 0.5f, cz);
                solids.add(floor);
                floorInst[x][y] = floor;

                if (solid) {
                    Model wallM = ((x + y) % 3 == 0) ? wallModelB : wallModelA;
                    ModelInstance wall = new ModelInstance(wallM);
                    wall.transform.setToTranslation(cx, WALL_H * 0.5f + FLOOR_H, cz);
                    solids.add(wall);
                    wallInst[x][y] = wall;
                } else if ((x + y * 2) % 19 == 0) {
                    ModelInstance prop = assets.meshes.create((x + y) % 2 == 0 ? "crate" : "barrel");
                    if (prop != null) {
                        float s = 22f;
                        prop.transform.setToTranslation(cx, FLOOR_H, cz).scale(s, s, s);
                        props.add(prop);
                    }
                }
            }
        }
    }

    private void drawUnitMeshes(World world, String characterId, ChromaPreset chroma) {
        Player player = world.getPlayer();
        float pScale = UNIT_SCALE * (player.isInvulnerable() ? 1.08f : 1f);
        ModelInstance playerMesh = assets.meshes.createPlayer(characterId);
        placeInstance(playerMesh, player.x, player.y, pScale, player.aimAngleDeg, chroma.tint, true, player.moving);

        for (Enemy enemy : world.getEnemies()) {
            String key = enemyKey(enemy.role);
            float scale = UNIT_SCALE * enemy.scale * ("boss".equals(key) ? 1.45f : 1f);
            ModelInstance mesh = assets.meshes.create(key);
            placeInstance(mesh, enemy.x, enemy.y, scale, enemy.aimAngleDeg, roleTint(enemy.role), false, enemy.moving);
        }
    }

    private void placeInstance(
            ModelInstance inst,
            float x,
            float z,
            float scale,
            float aimDeg,
            Color tint,
            boolean softTint,
            boolean moving
    ) {
        if (inst == null) {
            return;
        }
        float bob = moving ? (float) Math.sin(animTime * 14f) * 2.2f : 0f;
        float sway = moving ? (float) Math.sin(animTime * 10f) * 4f : 0f;
        inst.transform.setToTranslation(x, FLOOR_H + bob, z);
        inst.transform.rotate(Vector3.Y, -aimDeg + MODEL_YAW_OFFSET + sway);
        inst.transform.scale(scale, scale, scale);
        if (softTint) {
            // Mild multiply so chroma reads without wiping albedo
            Color soft = new Color(
                    0.55f + 0.45f * tint.r,
                    0.55f + 0.45f * tint.g,
                    0.55f + 0.45f * tint.b,
                    1f);
            for (Material mat : inst.materials) {
                mat.set(PBRColorAttribute.createBaseColorFactor(soft));
            }
        }
        unitBatch.render(inst, environment);
    }

    private Model box(float w, float h, float d, Texture tex, Color tint) {
        Material mat = new Material(
                TextureAttribute.createDiffuse(tex),
                ColorAttribute.createDiffuse(tint)
        );
        return modelBuilder.createBox(w, h, d, mat,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
    }

    private void drawProjectileDecals(World world) {
        for (Projectile shot : world.getProjectiles()) {
            Texture tex = shot.owner == Projectile.Owner.PLAYER ? assets.vfxMuzzle : assets.vfxMagic;
            Color c = shot.owner == Projectile.Owner.PLAYER
                    ? new Color(1f, 0.95f, 0.4f, 1f)
                    : new Color(1f, 0.3f, 0.5f, 1f);
            addBillboard(tex, shot.x, 14f, shot.y, 22f, c, 0f);
        }
    }

    private void drawBurstDecals(World world, ChromaPreset chroma) {
        for (VfxBurst b : world.getBursts()) {
            float p = Math.max(0.05f, b.life / b.maxLife);
            float size = 42f * (1.35f - p);
            Color c = new Color(b.r, b.g, b.b, p);
            if (b.g > 0.8f) {
                c.set(chroma.tint.r, chroma.tint.g, chroma.tint.b, p);
            }
            addBillboard(assets.vfxCircle, b.x, 20f, b.y, size, c, 0f);
        }
        // particle bus as small decals
        // (ParticleBus draws in 2D; also emit a few via its positions — skipped for brevity)
    }

    private void addBillboard(Texture tex, float x, float y, float z, float size, Color tint, float aimDeg) {
        if (tex == null) {
            return;
        }
        Decal d = Decal.newDecal(size, size, new TextureRegion(tex), true);
        d.setColor(tint.r, tint.g, tint.b, tint.a);
        d.setPosition(x, y, z);
        // face camera
        d.lookAt(decalStrategy.getCamera().position, Vector3.Y);
        // slight yaw from aim so facing reads
        if (Math.abs(aimDeg) > 0.01f) {
            d.rotateY(-aimDeg);
        }
        decalBatch.add(d);
    }

    private void clearBake() {
        solids.clear();
        props.clear();
        for (Model m : ownedModels) {
            m.dispose();
        }
        ownedModels.clear();
        floorInst = null;
        wallInst = null;
        bakedMap = null;
    }

    private static String enemyKey(EnemyRole role) {
        return switch (role) {
            case SHOOTER -> "shooter";
            case HUNT_TARGET -> "hunt";
            case DRONE, PATCHER -> "drone";
            case SHIELDER -> "shielder";
            case WARDEN, ECHO, GRID_ROOT -> "boss";
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

    @Override
    public void dispose() {
        clearBake();
        worldBatch.dispose();
        unitBatch.dispose();
        if (decalBatch != null) {
            decalBatch.dispose();
        }
        if (decalStrategy != null) {
            decalStrategy.dispose();
        }
    }
}
