package com.edicius.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Disposable;
import com.edicius.gfx.GameAssets;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;

/** Offscreen rotating GLB preview for character select. */
public class ShellPreview implements Disposable {

    private static final int SIZE = 512;

    private final GameAssets assets;
    private final PerspectiveCamera camera = new PerspectiveCamera(32f, SIZE, SIZE);
    private final ModelBatch batch;
    private final Environment environment = new Environment();
    private FrameBuffer fbo;
    private TextureRegion region;
    private float yaw;
    private String shellId = "cipher";
    private Color chroma = Color.WHITE;

    public ShellPreview(GameAssets assets) {
        this.assets = assets;
        this.batch = new ModelBatch(new PBRShaderProvider(PBRShaderProvider.createDefaultConfig()));
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.62f, 0.7f, 1f));
        environment.add(new DirectionalLight().set(1f, 1f, 1f, -0.4f, -1f, -0.3f));
        environment.add(new DirectionalLight().set(0.45f, 0.35f, 0.7f, 0.5f, -0.2f, 0.4f));
        camera.near = 0.1f;
        camera.far = 100f;
        camera.position.set(2.4f, 1.6f, 2.4f);
        camera.lookAt(0f, 0.85f, 0f);
        camera.up.set(0f, 1f, 0f);
        camera.update();
        ensureFbo();
    }

    public void setShell(String shellId, Color chroma) {
        this.shellId = shellId;
        this.chroma = chroma;
    }

    public TextureRegion region() {
        return region;
    }

    public void update(float dt) {
        yaw += dt * 35f;
        ensureFbo();
        fbo.begin();
        Gdx.gl.glClearColor(0.04f, 0.05f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        ModelInstance inst = assets.meshes.createPlayer(shellId);
        if (inst != null) {
            inst.transform.idt()
                    .rotate(0f, 1f, 0f, yaw)
                    .scale(1.15f, 1.15f, 1.15f);
            Color soft = new Color(
                    0.55f + 0.45f * chroma.r,
                    0.55f + 0.45f * chroma.g,
                    0.55f + 0.45f * chroma.b,
                    1f);
            for (Material mat : inst.materials) {
                mat.set(PBRColorAttribute.createBaseColorFactor(soft));
            }
            batch.begin(camera);
            batch.render(inst, environment);
            batch.end();
        }
        fbo.end();
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }

    private void ensureFbo() {
        if (fbo != null) {
            return;
        }
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, SIZE, SIZE, true);
        region = new TextureRegion(fbo.getColorBufferTexture());
        region.flip(false, true);
    }

    @Override
    public void dispose() {
        if (fbo != null) {
            fbo.dispose();
            fbo = null;
        }
        batch.dispose();
    }
}
