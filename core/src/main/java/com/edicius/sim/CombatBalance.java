package com.edicius.sim;

/** Tuned Act pacing values collected in one place. */
public final class CombatBalance {

    private CombatBalance() {
    }

    public static final int PLAYER_MAX_HP = 120;
    public static final float PLAYER_SPEED = 270f;
    public static final float DASH_DISTANCE = 210f;
    public static final float DASH_DURATION = 0.14f;
    public static final int ENEMY_SHOT_DAMAGE = 8;
    public static final float WARDEN_TELEGRAPH = 1.15f;
    public static final float CONTACT_COOLDOWN = 0.55f;
    /** Hold timers ease up across acts. */
    public static final float HOLD_ACT1 = 18f;
    public static final float HOLD_ACT2 = 24f;
    public static final float HOLD_ACT3 = 30f;
}
