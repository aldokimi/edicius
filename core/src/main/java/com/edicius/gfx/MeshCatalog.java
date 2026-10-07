package com.edicius.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Disposable;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import java.util.HashMap;
import java.util.Map;

/** Quaternius (and drop-in) glTF meshes for units and props. */
public class MeshCatalog implements Disposable {

    private final Map<String, SceneAsset> assets = new HashMap<>();
    private boolean loaded;

    public void load() {
        if (loaded) {
            return;
        }
        loadOne("player", "runtime/models/Enemy_QuadShell.gltf");
        loadOne("grunt", "runtime/models/Enemy_Trilobite.gltf");
        loadOne("shooter", "runtime/models/Enemy_EyeDrone.gltf");
        loadOne("drone", "runtime/models/Enemy_EyeDrone.gltf");
        loadOne("boss", "runtime/models/Enemy_QuadShell.gltf");
        loadOne("crate", "runtime/models/Prop_Crate.gltf");
        loadOne("barrel", "runtime/models/Prop_Barrel1.gltf");
        loaded = true;
    }

    private void loadOne(String key, String path) {
        if (!Gdx.files.internal(path).exists()) {
            Gdx.app.error("MeshCatalog", "Missing mesh: " + path);
            return;
        }
        try {
            SceneAsset asset = new GLTFLoader().load(Gdx.files.internal(path));
            assets.put(key, asset);
        } catch (Exception e) {
            Gdx.app.error("MeshCatalog", "Failed to load " + path, e);
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

    @Override
    public void dispose() {
        for (SceneAsset a : assets.values()) {
            a.dispose();
        }
        assets.clear();
        loaded = false;
    }
}
