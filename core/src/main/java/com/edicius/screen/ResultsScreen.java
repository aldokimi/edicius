package com.edicius.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.edicius.EdiciusGame;
import com.edicius.run.RunSession;
import com.edicius.run.StyleRank;

public class ResultsScreen extends ScreenAdapter {

    private final EdiciusGame game;

    public ResultsScreen(EdiciusGame game) {
        this.game = game;
        RunSession s = game.session;
        String chapterId = s.chapter != null ? s.chapter.id : "endless";
        game.meta.awardRun(s.loadout.kills, s.victory, chapterId, s.loadout.styleScore);
        int score = s.loadout.kills * 10 + (int) s.loadout.styleScore + s.endlessDepth * 25;
        String label = s.mode == RunSession.Mode.DAILY ? "daily"
                : (s.mode == RunSession.Mode.ENDLESS ? "endless" : chapterId);
        game.leaderboards.submit(label, score, s.seed);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.02f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        if (game.session.failed && game.assets.gameOverBg != null) {
            game.batch.setColor(1f, 1f, 1f, 0.65f);
            game.batch.draw(game.assets.gameOverBg, 0, 0, 1280, 720);
            game.batch.setColor(Color.WHITE);
        }
        game.font.setColor(Color.CYAN);
        game.font.getData().setScale(1.7f);
        game.font.draw(game.batch, game.session.failed ? "SIGNAL LOST" : "RAID REPORT", 80, 600);
        game.font.getData().setScale(1.15f);
        game.font.setColor(Color.WHITE);
        game.font.draw(game.batch, game.session.resultsSummary(), 80, 520);
        game.font.draw(game.batch, "Style rank " + StyleRank.rank(game.session.loadout.styleScore), 80, 480);
        game.font.draw(game.batch, "Protocol points: " + game.meta.protocolPoints, 80, 440);
        if (game.session.victory && game.session.chapter != null) {
            game.font.setColor(Color.ORANGE);
            game.font.draw(game.batch, "Unlock: " + game.session.chapter.unlocks, 80, 400);
        }
        game.font.setColor(Color.LIGHT_GRAY);
        game.font.draw(game.batch, "ENTER — Menu", 80, 200);
        game.batch.end();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new MainMenuScreen(game, game.session.resultsSummary()));
        }
    }
}
