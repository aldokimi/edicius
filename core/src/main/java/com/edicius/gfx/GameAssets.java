package com.edicius.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;

public class GameAssets {

    public Texture floor;
    public Texture wall;
    public Texture daveStand;
    public Texture daveWalk;
    public Texture mattStand;
    public Texture mattWalk;
    public Texture shot;
    public Texture menuBg;
    public Texture gameOverBg;
    public Texture fireBall;

    public void load() {
        floor = tex("sprites/tiles/floor.jpg");
        wall = tex("sprites/tiles/wall.png");
        daveStand = tex("sprites/units/dave/stand.png");
        daveWalk = tex("sprites/units/dave/walk.png");
        mattStand = tex("sprites/units/matt/stand.png");
        mattWalk = tex("sprites/units/matt/walk.png");
        shot = tex("sprites/units/shot/stand.png");
        menuBg = tex("sprites/btns/gameBackground.png");
        gameOverBg = tex("sprites/gameOverBG.jpg");
        fireBall = tex("sprites/fireBall.png");
        try {
            Pixmap pm = new Pixmap(Gdx.files.internal("sprites/cursor.png"));
            Cursor cursor = Gdx.graphics.newCursor(pm, 0, 0);
            Gdx.graphics.setCursor(cursor);
            pm.dispose();
        } catch (Exception ignored) {
            // default cursor
        }
    }

    private static Texture tex(String path) {
        Texture t = new Texture(Gdx.files.internal(path));
        t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        return t;
    }

    public void dispose() {
        dispose(floor, wall, daveStand, daveWalk, mattStand, mattWalk, shot, menuBg, gameOverBg, fireBall);
    }

    private static void dispose(Texture... textures) {
        for (Texture t : textures) {
            if (t != null) {
                t.dispose();
            }
        }
    }
}
