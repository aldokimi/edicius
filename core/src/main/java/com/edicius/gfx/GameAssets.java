package com.edicius.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;
import java.util.Map;

public class GameAssets {

    public final Map<String, UnitSkin> characters = new HashMap<>();
    public final Map<String, UnitSkin> enemies = new HashMap<>();

    public Texture floorA;
    public Texture floorB;
    public Texture wallA;
    public Texture wallB;
    public Texture accentTile;

    public Texture vfxSpark;
    public Texture vfxStar;
    public Texture vfxMuzzle;
    public Texture vfxFlare;
    public Texture vfxMagic;
    public Texture vfxCircle;

    public Texture menuBg;
    public Texture gameOverBg;
    public Texture shotFallback;

    /** Legacy sheets kept as emergency fallback. */
    public Texture daveStand;
    public Texture daveWalk;
    public Texture mattStand;
    public Texture mattWalk;

    public void load() {
        for (CharacterShell shell : CharacterCatalog.SHELLS) {
            characters.put(shell.id, loadUnit(shell.standPath, shell.armedPath));
        }
        enemies.put("grunt", loadUnit("runtime/enemies/grunt/stand.png", "runtime/enemies/grunt/armed.png"));
        enemies.put("shooter", loadUnit("runtime/enemies/shooter/stand.png", "runtime/enemies/shooter/armed.png"));
        enemies.put("drone", loadUnit("runtime/enemies/drone/stand.png", "runtime/enemies/drone/armed.png"));
        enemies.put("boss", loadUnit("runtime/enemies/boss/stand.png", "runtime/enemies/boss/armed.png"));

        floorA = tex("runtime/tiles/floor_a.png");
        floorB = tex("runtime/tiles/floor_b.png");
        wallA = tex("runtime/tiles/wall_a.png");
        wallB = tex("runtime/tiles/wall_b.png");
        accentTile = tex("runtime/tiles/accent.png");

        vfxSpark = tex("runtime/vfx/spark_01.png");
        vfxStar = tex("runtime/vfx/star_01.png");
        vfxMuzzle = tex("runtime/vfx/muzzle_01.png");
        vfxFlare = tex("runtime/vfx/flare_01.png");
        vfxMagic = tex("runtime/vfx/magic_05.png");
        vfxCircle = tex("runtime/vfx/circle_05.png");

        menuBg = tex("sprites/btns/gameBackground.png");
        gameOverBg = tex("sprites/gameOverBG.jpg");
        shotFallback = tex("sprites/units/shot/stand.png");
        daveStand = softTex("sprites/units/dave/stand.png");
        daveWalk = softTex("sprites/units/dave/walk.png");
        mattStand = softTex("sprites/units/matt/stand.png");
        mattWalk = softTex("sprites/units/matt/walk.png");

        try {
            Pixmap pm = new Pixmap(Gdx.files.internal("sprites/cursor.png"));
            Cursor cursor = Gdx.graphics.newCursor(pm, 0, 0);
            Gdx.graphics.setCursor(cursor);
            pm.dispose();
        } catch (Exception ignored) {
        }
    }

    public UnitSkin playerSkin(String id) {
        UnitSkin skin = characters.get(id);
        return skin != null ? skin : characters.get(CharacterCatalog.SHELLS.get(0).id);
    }

    public UnitSkin enemySkin(String roleKey) {
        UnitSkin skin = enemies.get(roleKey);
        return skin != null ? skin : enemies.get("grunt");
    }

    private static UnitSkin loadUnit(String standPath, String armedPath) {
        return new UnitSkin(tex(standPath), softTex(armedPath));
    }

    private static Texture tex(String path) {
        Texture t = new Texture(Gdx.files.internal(path));
        t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return t;
    }

    private static Texture softTex(String path) {
        try {
            if (!Gdx.files.internal(path).exists()) {
                return null;
            }
            return tex(path);
        } catch (Exception e) {
            return null;
        }
    }

    public void dispose() {
        for (UnitSkin s : characters.values()) s.dispose();
        for (UnitSkin s : enemies.values()) s.dispose();
        dispose(floorA, floorB, wallA, wallB, accentTile, vfxSpark, vfxStar, vfxMuzzle, vfxFlare, vfxMagic,
                vfxCircle, menuBg, gameOverBg, shotFallback, daveStand, daveWalk, mattStand, mattWalk);
    }

    private static void dispose(Texture... textures) {
        for (Texture t : textures) {
            if (t != null) t.dispose();
        }
    }
}
