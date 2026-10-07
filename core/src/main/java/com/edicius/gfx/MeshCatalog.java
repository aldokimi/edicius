package com.edicius.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Disposable;
import net.mgsx.gltf.loaders.glb.GLBLoader;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import java.util.HashMap;
import java.util.Map;

/** 3D unit/prop meshes (Kenney Blocky GLB + Quaternius props). */
public class MeshCatalog implements Disposable {

    private final Map<String, SceneAsset> assets = new HashMap<>();
    private boolean loaded;

    public void load() {
        if (loaded) {
            return;
        }
        // Humanoid characters (clearly 3D)
        loadGlb("cipher", "runtime/models/chars/cipher.glb");
        loadGlb("neonjack", "runtime/models/chars/neonjack.glb");
        loadGlb("grit", "runtime/models/chars/grit.glb");
        loadGlb("null7", "runtime/models/chars/null7.glb");
        loadGlb("proxy", "runtime/models/chars/proxy.glb");
        loadGlb("apex", "runtime/models/chars/apex.glb");
        loadGlb("grunt", "runtime/models/chars/grunt.glb");
        loadGlb("shooter", "runtime/models/chars/shooter.glb");
        loadGlb("drone", "runtime/models/chars/drone.glb");
        loadGlb("boss", "runtime/models/chars/boss.glb");
        loadGlb("hunt", "runtime/models/chars/hunt.glb");
        loadGlb("shielder", "runtime/models/chars/shielder.glb");
        // Player alias → default shell mesh
        alias("player", "cipher");

        // Props (Quaternius)
        loadGltf("crate", "runtime/models/Prop_Crate.gltf");
        loadGltf("barrel", "runtime/models/Prop_Barrel1.gltf");
        loaded = true;
        Gdx.app.log("MeshCatalog", "Loaded " + assets.size() + " meshes");
    }

    private void alias(String key, String target) {
        SceneAsset a = assets.get(target);
        if (a != null) {
            assets.put(key, a);
        }
    }

    private void loadGlb(String key, String path) {
        if (!Gdx.files.internal(path).exists()) {
            Gdx.app.error("MeshCatalog", "Missing: " + path);
            return;
        }
        try {
            assets.put(key, new GLBLoader().load(Gdx.files.internal(path)));
        } catch (Exception e) {
            Gdx.app.error("MeshCatalog", "Failed GLB " + path, e);
        }
    }

    private void loadGltf(String key, String path) {
        if (!Gdx.files.internal(path).exists()) {
            Gdx.app.error("MeshCatalog", "Missing: " + path);
            return;
        }
        try {
            assets.put(key, new GLTFLoader().load(Gdx.files.internal(path)));
        } catch (Exception e) {
            Gdx.app.error("MeshCatalog", "Failed glTF " + path, e);
        }
    }

    public boolean has(String key) {
        return assets.containsKey(key);
    }

    public ModelInstance create(String key) {
        SceneAsset asset = assets.get(key);
        if (asset == null || asset.scene == null || asset.scene.model == null) {
            return null;
        }
        return new ModelInstance(asset.scene.model);
    }

    /** Prefer shell-specific mesh, else generic player. */
    public ModelInstance createPlayer(String shellId) {
        ModelInstance inst = create(shellId);
        return inst != null ? inst : create("player");
    }

    @Override
    public void dispose() {
        // Aliases share SceneAsset refs — dispose unique only
        java.util.HashSet<SceneAsset> unique = new java.util.HashSet<>(assets.values());
        for (SceneAsset a : unique) {
            a.dispose();
        }
        assets.clear();
        loaded = false;
    }
}
