package com.edicius.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.edicius.sim.Player;
import com.edicius.sim.World;

public class HudRenderer {

    public void renderShapes(ShapeRenderer shapes, World world, float screenW, float screenH) {
        Player player = world.getPlayer();
        float barW = 220f;
        float barH = 18f;
        float x = 20f;
        float y = screenH - 36f;
        float pct = player.hp / (float) Player.MAX_HP;

        shapes.setColor(0.15f, 0.15f, 0.2f, 1f);
        shapes.rect(x, y, barW, barH);
        shapes.setColor(0.1f, 0.85f, 0.55f, 1f);
        shapes.rect(x, y, barW * pct, barH);
    }

    public void renderText(SpriteBatch batch, BitmapFont font, World world, float screenW, float screenH) {
        font.getData().setScale(1.1f);
        font.setColor(Color.WHITE);
        font.draw(batch, "HP " + world.getPlayer().hp, 24, screenH - 42);
        font.draw(batch, "Kills " + world.getPlayer().kills, screenW - 140, screenH - 24);
        font.draw(batch, "SPACE dash | LMB shoot | Esc pause", 20, 28);
        if (world.isPaused()) {
            font.setColor(Color.YELLOW);
            font.getData().setScale(2f);
            font.draw(batch, "PAUSED", screenW * 0.5f - 70, screenH * 0.5f);
            font.getData().setScale(1.1f);
        }
        if (world.isCleared()) {
            font.setColor(Color.CYAN);
            font.getData().setScale(1.6f);
            font.draw(batch, "SECTOR CLEAR — ENTER", screenW * 0.5f - 140, screenH * 0.5f);
            font.getData().setScale(1.1f);
        }
    }
}
