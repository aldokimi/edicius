package com.edicius.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioBus {

    private Music music;
    private Sound shoot;
    private Sound hit;
    private Sound dash;
    private Sound clear;
    private boolean muted;

    public void load() {
        try {
            music = Gdx.audio.newMusic(Gdx.files.internal("audio/game_music.wav"));
            music.setLooping(true);
            music.setVolume(0.35f);
        } catch (Exception e) {
            Gdx.app.error("AudioBus", "music missing", e);
        }
        shoot = loadSound("audio/shoot.wav");
        hit = loadSound("audio/hit.wav");
        dash = loadSound("audio/dash.wav");
        clear = loadSound("audio/clear.wav");
    }

    private static Sound loadSound(String path) {
        try {
            return Gdx.audio.newSound(Gdx.files.internal(path));
        } catch (Exception e) {
            return null;
        }
    }

    public void startMusic() {
        if (!muted && music != null && !music.isPlaying()) {
            music.play();
        }
    }

    public void stopMusic() {
        if (music != null) {
            music.stop();
        }
    }

    public void toggleMute() {
        muted = !muted;
        if (muted) {
            stopMusic();
        } else {
            startMusic();
        }
    }

    public boolean isMuted() {
        return muted;
    }

    public void playShoot() {
        play(shoot, 0.35f);
    }

    public void playHit() {
        play(hit, 0.4f);
    }

    public void playDash() {
        play(dash, 0.45f);
    }

    public void playRoomClear() {
        play(clear, 0.5f);
    }

    private void play(Sound s, float volume) {
        if (!muted && s != null) {
            s.play(volume);
        }
    }

    public void dispose() {
        if (music != null) music.dispose();
        if (shoot != null) shoot.dispose();
        if (hit != null) hit.dispose();
        if (dash != null) dash.dispose();
        if (clear != null) clear.dispose();
    }
}
