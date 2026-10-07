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
        float barW = 240f;
        float barH = 18f;
        float x = 20f;
        float y = screenH - 36f;
        float pct = player.hp / (float) Math.max(1, player.maxHp);

        shapes.setColor(0.1f, 0.12f, 0.18f, 0.85f);
        shapes.rect(x - 4, y - 4, barW + 8, barH + 8);
        shapes.setColor(0.15f, 0.15f, 0.2f, 1f);
        shapes.rect(x, y, barW, barH);
        shapes.setColor(0.15f, 0.9f, 0.65f, 1f);
        shapes.rect(x, y, barW * pct, barH);
    }

    public void renderText(SpriteBatch batch, BitmapFont font, World world, float screenW, float screenH, boolean muted) {
        Player player = world.getPlayer();
        font.getData().setScale(1.05f);
        font.setColor(Color.WHITE);
        font.draw(batch, "HP " + player.hp + "/" + player.maxHp, 24, screenH - 42);
        font.draw(batch, "Kills " + player.kills, screenW - 140, screenH - 24);
        font.draw(batch, "Style " + StyleRank.rank(player.styleScore)
                + (player.combo > 1 ? "  x" + player.combo : ""), screenW - 210, screenH - 50);
        font.setColor(new Color(0.55f, 0.95f, 1f, 1f));
        font.draw(batch, world.getRoom().title + " [" + world.getRoom().type + "]", 20, screenH - 70);
        font.setColor(Color.WHITE);
        font.draw(batch, "WPN " + player.weapon.displayName + "  (1/2/3)", 20, screenH - 94);
        if (world.getMutator() != null) {
            font.setColor(Color.ORANGE);
            font.draw(batch, world.getMutator().label, 20, screenH - 118);
        }
        font.setColor(Color.LIGHT_GRAY);
        if (world.getRoom().type == ArenaType.HOLD) {
            font.draw(batch, "Hold " + String.format("%.1f", Math.max(0, world.getHoldTimer())) + "s", 20, 52);
        } else if (world.getRoom().type == ArenaType.HACK) {
            font.draw(batch, "Hack " + String.format("%.0f", Math.min(100, world.getHackProgress() * 10)) + "%  (node + E)", 20, 52);
        } else if (world.getRoom().type == ArenaType.HUNT) {
            font.draw(batch, "Hunt the marked target", 20, 52);
        }
        font.draw(batch, "SPACE dash | LMB shoot | Esc pause | M mute" + (muted ? " [MUTED]" : ""), 20, 28);
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

    public void renderTutorial(SpriteBatch batch, BitmapFont font, float screenW, float screenH) {
        font.getData().setScale(1.15f);
        font.setColor(new Color(0.1f, 0.12f, 0.2f, 0.85f));
        // text only; panel drawn as lines
        font.setColor(Color.CYAN);
        font.draw(batch, "NYX // QUICKSTART", screenW * 0.5f - 120, screenH * 0.72f);
        font.setColor(Color.WHITE);
        font.draw(batch, "WASD move  ·  mouse aim  ·  LMB fire", screenW * 0.5f - 180, screenH * 0.66f);
        font.draw(batch, "SPACE dash (i-frames)  ·  1/2/3 weapons", screenW * 0.5f - 180, screenH * 0.62f);
        font.draw(batch, "Clear rooms, pick upgrades, delete Warden-01", screenW * 0.5f - 180, screenH * 0.58f);
        font.setColor(Color.ORANGE);
        font.draw(batch, "ENTER to dismiss", screenW * 0.5f - 80, screenH * 0.52f);
        font.getData().setScale(1.05f);
    }
}
