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
        float barW = 260f;
        float barH = 20f;
        float x = 20f;
        float y = screenH - 40f;
        float pct = player.hp / (float) Math.max(1, player.maxHp);

        shapes.setColor(0.08f, 0.12f, 0.2f, 0.9f);
        shapes.rect(x - 5, y - 5, barW + 10, barH + 10);
        shapes.setColor(0.12f, 0.14f, 0.22f, 1f);
        shapes.rect(x, y, barW, barH);
        shapes.setColor(0.2f, 0.95f, 0.75f, 1f);
        shapes.rect(x, y, barW * pct, barH);
    }

    public void renderText(SpriteBatch batch, BitmapFont font, World world, float screenW, float screenH, boolean muted) {
        Player player = world.getPlayer();
        font.setColor(Color.WHITE);
        font.draw(batch, "HP " + player.hp + "/" + player.maxHp, 24, screenH - 48);
        font.draw(batch, "Kills " + player.kills, screenW - 160, screenH - 28);
        font.draw(batch, "Style " + StyleRank.rank(player.styleScore)
                + (player.combo > 1 ? "  x" + player.combo : ""), screenW - 230, screenH - 54);
        font.setColor(new Color(0.55f, 0.95f, 1f, 1f));
        font.draw(batch, world.getRoom().title + " [" + world.getRoom().type + "]", 20, screenH - 78);
        font.setColor(Color.WHITE);
        font.draw(batch, "WPN " + player.weapon.displayName + "  (1/2/3)", 20, screenH - 104);
        if (world.getMutator() != null) {
            font.setColor(Color.ORANGE);
            font.draw(batch, world.getMutator().label, 20, screenH - 130);
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
    }
}
