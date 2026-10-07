package com.edicius.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.edicius.run.StyleRank;
import com.edicius.sim.ArenaType;
import com.edicius.sim.Player;
import com.edicius.sim.World;

public class HudRenderer {

    public void renderShapes(ShapeRenderer shapes, World world, float screenW, float screenH) {
        Player player = world.getPlayer();
        float barW = 220f;
        float barH = 18f;
        float x = 20f;
        float y = screenH - 36f;
        float pct = player.hp / (float) Math.max(1, player.maxHp);

        shapes.setColor(0.15f, 0.15f, 0.2f, 1f);
        shapes.rect(x, y, barW, barH);
        shapes.setColor(0.1f, 0.85f, 0.55f, 1f);
        shapes.rect(x, y, barW * pct, barH);
    }

    public void renderText(SpriteBatch batch, BitmapFont font, World world, float screenW, float screenH) {
        Player player = world.getPlayer();
        font.getData().setScale(1.05f);
        font.setColor(Color.WHITE);
        font.draw(batch, "HP " + player.hp + "/" + player.maxHp, 24, screenH - 42);
        font.draw(batch, "Kills " + player.kills, screenW - 140, screenH - 24);
        font.draw(batch, "Style " + StyleRank.rank(player.styleScore)
                + (player.combo > 1 ? "  x" + player.combo : ""), screenW - 200, screenH - 50);
        font.draw(batch, world.getRoom().title + " [" + world.getRoom().type + "]", 20, screenH - 70);
        font.draw(batch, "WPN " + player.weapon.displayName + "  (1/2/3)", 20, screenH - 94);
        if (world.getMutator() != null) {
            font.setColor(Color.ORANGE);
            font.draw(batch, world.getMutator().label, 20, screenH - 118);
        }
        font.setColor(Color.LIGHT_GRAY);
        if (world.getRoom().type == ArenaType.HOLD) {
            font.draw(batch, "Hold " + String.format("%.1f", Math.max(0, world.getHoldTimer())) + "s", 20, 52);
        } else if (world.getRoom().type == ArenaType.HACK) {
            font.draw(batch, "Hack " + String.format("%.0f", world.getHackProgress() * 10) + "%  (stand on node, E boosts)", 20, 52);
        } else if (world.getRoom().type == ArenaType.HUNT) {
            font.draw(batch, "Hunt the marked target", 20, 52);
        }
        font.draw(batch, "SPACE dash | LMB shoot | Esc pause", 20, 28);
        if (world.isPaused()) {
            font.setColor(Color.YELLOW);
            font.getData().setScale(2f);
            font.draw(batch, "PAUSED", screenW * 0.5f - 70, screenH * 0.5f);
            font.getData().setScale(1.05f);
        }
        if (world.isCleared()) {
            font.setColor(Color.CYAN);
            font.getData().setScale(1.4f);
            font.draw(batch, "ROOM CLEAR — ENTER", screenW * 0.5f - 140, screenH * 0.5f);
            font.getData().setScale(1.05f);
        }
    }
}
