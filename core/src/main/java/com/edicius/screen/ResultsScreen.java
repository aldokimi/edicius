package com.edicius.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.edicius.EdiciusGame;
import com.edicius.run.RunSession;
import com.edicius.run.StyleRank;
import com.edicius.ui.MenuStageScreen;
import com.edicius.ui.UiTheme;

public class ResultsScreen extends MenuStageScreen {

    public ResultsScreen(EdiciusGame game) {
        super(game);
        RunSession s = game.session;
        String chapterId = s.chapter != null ? s.chapter.id : "endless";
        game.meta.awardRun(s.loadout.kills, s.victory, chapterId, s.loadout.styleScore);
        int score = s.loadout.kills * 10 + (int) s.loadout.styleScore + s.endlessDepth * 25;
        String label = s.mode == RunSession.Mode.DAILY ? "daily"
                : (s.mode == RunSession.Mode.ENDLESS ? "endless" : chapterId);
        game.leaderboards.submit(label, score, s.seed);
    }

    @Override
    protected void drawBackground(float r, float g, float b) {
        super.drawBackground(0.05f, 0.02f, 0.08f);
        if (game.session.failed && game.assets.gameOverBg != null) {
            game.batch.setProjectionMatrix(camera.combined);
            game.batch.begin();
            game.batch.setColor(1f, 1f, 1f, 0.55f);
            game.batch.draw(game.assets.gameOverBg, 0, 0, UiTheme.VIEW_W, UiTheme.VIEW_H);
            game.batch.setColor(Color.WHITE);
            game.batch.end();
        }
    }

    @Override
    protected void buildUi(Table root) {
        root.pad(48, 70, 40, 70).top().left();
        Label title = new Label(game.session.failed ? "SIGNAL LOST" : "RAID REPORT", game.ui.skin, "heading");
        title.setColor(game.session.failed ? new Color(1f, 0.4f, 0.45f, 1f) : new Color(0.35f, 0.95f, 1f, 1f));
        root.add(title).left().padBottom(18).row();
        root.add(new Label(game.session.resultsSummary(), game.ui.skin, "default")).left().padBottom(8).row();
        root.add(new Label("Style rank " + StyleRank.rank(game.session.loadout.styleScore), game.ui.skin, "default"))
                .left().padBottom(8).row();
        root.add(new Label("Protocol points: " + game.meta.protocolPoints, game.ui.skin, "accent"))
                .left().padBottom(8).row();
        if (game.session.victory && game.session.chapter != null) {
            root.add(new Label("Unlock: " + game.session.chapter.unlocks, game.ui.skin, "accent"))
                    .left().padBottom(16).row();
        }

        TextButton menu = new TextButton("Menu", game.ui.skin);
        menu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toMenu();
            }
        });
        root.add(menu).width(220).height(48).left().padTop(24).row();
    }

    private void toMenu() {
        game.setScreen(new MainMenuScreen(game, game.session.resultsSummary()));
    }

    @Override
    protected void handleShortcuts() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            toMenu();
        }
    }
}
