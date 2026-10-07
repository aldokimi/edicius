package com.edicius.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.List;

/** FreeType fonts + procedural neon Scene2D skin. */
public class UiTheme implements Disposable {

    public static final float VIEW_W = 1280f;
    public static final float VIEW_H = 720f;

    public final BitmapFont titleFont;
    public final BitmapFont headingFont;
    public final BitmapFont bodyFont;
    public final BitmapFont hudFont;
    public final Skin skin;

    /** Legacy alias used by HUD / older call sites. */
    public final BitmapFont font;

    private final List<Texture> ownedTextures = new ArrayList<>();
    private final List<FreeTypeFontGenerator> generators = new ArrayList<>();

    public UiTheme() {
        FreeTypeFontGenerator orbitron = generator("fonts/Orbitron-Bold.ttf");
        FreeTypeFontGenerator exo = generator("fonts/Exo2-Regular.ttf");
        FreeTypeFontGenerator exoSemi = generator("fonts/Exo2-SemiBold.ttf");

        titleFont = bake(orbitron, 54, Color.WHITE);
        headingFont = bake(orbitron, 32, Color.WHITE);
        bodyFont = bake(exo, 22, Color.WHITE);
        hudFont = bake(exoSemi, 20, Color.WHITE);
        font = hudFont;

        skin = buildSkin();
    }

    private FreeTypeFontGenerator generator(String path) {
        FreeTypeFontGenerator g = new FreeTypeFontGenerator(Gdx.files.internal(path));
        generators.add(g);
        return g;
    }

    private static BitmapFont bake(FreeTypeFontGenerator gen, int size, Color color) {
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.size = size;
        p.color = color;
        p.borderWidth = size >= 40 ? 1.5f : 0.8f;
        p.borderColor = new Color(0.02f, 0.08f, 0.18f, 0.9f);
        p.shadowOffsetX = 1;
        p.shadowOffsetY = 1;
        p.shadowColor = new Color(0f, 0f, 0f, 0.45f);
        p.spaceX = 1;
        p.characters = FreeTypeFontGenerator.DEFAULT_CHARS
                + "—–·×÷…“”‘’•™©®°±µπ";
        BitmapFont font = gen.generateFont(p);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return font;
    }

    private Skin buildSkin() {
        Skin s = new Skin();
        s.add("default-font", bodyFont, BitmapFont.class);
        s.add("title-font", titleFont, BitmapFont.class);
        s.add("heading-font", headingFont, BitmapFont.class);
        s.add("hud-font", hudFont, BitmapFont.class);

        Drawable up = panel(new Color(0.05f, 0.12f, 0.22f, 0.92f), new Color(0.2f, 0.85f, 1f, 0.95f), 3);
        Drawable over = panel(new Color(0.08f, 0.22f, 0.38f, 0.95f), new Color(0.45f, 1f, 1f, 1f), 3);
        Drawable down = panel(new Color(0.12f, 0.35f, 0.55f, 1f), new Color(1f, 0.85f, 0.25f, 1f), 3);
        Drawable disabled = panel(new Color(0.12f, 0.12f, 0.16f, 0.85f), new Color(0.35f, 0.35f, 0.4f, 0.8f), 2);

        TextButton.TextButtonStyle btn = new TextButton.TextButtonStyle();
        btn.up = up;
        btn.over = over;
        btn.down = down;
        btn.disabled = disabled;
        btn.font = bodyFont;
        btn.fontColor = Color.WHITE;
        btn.overFontColor = new Color(0.75f, 1f, 1f, 1f);
        btn.downFontColor = new Color(1f, 0.95f, 0.6f, 1f);
        btn.disabledFontColor = new Color(0.55f, 0.55f, 0.6f, 1f);
        s.add("default", btn);

        TextButton.TextButtonStyle danger = new TextButton.TextButtonStyle(btn);
        danger.up = panel(new Color(0.18f, 0.06f, 0.12f, 0.92f), new Color(1f, 0.35f, 0.55f, 0.95f), 3);
        danger.over = panel(new Color(0.28f, 0.1f, 0.18f, 0.95f), new Color(1f, 0.55f, 0.7f, 1f), 3);
        s.add("danger", danger);

        Label.LabelStyle body = new Label.LabelStyle(bodyFont, Color.WHITE);
        Label.LabelStyle title = new Label.LabelStyle(titleFont, new Color(0.35f, 0.95f, 1f, 1f));
        Label.LabelStyle heading = new Label.LabelStyle(headingFont, new Color(0.55f, 0.9f, 1f, 1f));
        Label.LabelStyle muted = new Label.LabelStyle(bodyFont, new Color(0.65f, 0.72f, 0.8f, 1f));
        Label.LabelStyle accent = new Label.LabelStyle(bodyFont, new Color(1f, 0.7f, 0.25f, 1f));
        s.add("default", body);
        s.add("title", title);
        s.add("heading", heading);
        s.add("muted", muted);
        s.add("accent", accent);

        s.add("panel", panel(new Color(0.04f, 0.07f, 0.14f, 0.82f), new Color(0.25f, 0.7f, 1f, 0.55f), 2));
        return s;
    }

    private Drawable panel(Color fill, Color border, int borderW) {
        int size = 32;
        Pixmap pm = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pm.setColor(border);
        pm.fill();
        pm.setColor(fill);
        pm.fillRectangle(borderW, borderW, size - borderW * 2, size - borderW * 2);
        Texture tex = new Texture(pm);
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        ownedTextures.add(tex);
        pm.dispose();
        NinePatch patch = new NinePatch(tex, 8, 8, 8, 8);
        return new NinePatchDrawable(patch);
    }

    @Override
    public void dispose() {
        // Skin owns the BitmapFonts registered above — do not dispose them twice.
        skin.dispose();
        for (Texture t : ownedTextures) {
            t.dispose();
        }
        ownedTextures.clear();
        for (FreeTypeFontGenerator g : generators) {
            g.dispose();
        }
        generators.clear();
    }
}
