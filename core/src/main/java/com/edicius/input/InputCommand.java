package com.edicius.input;

public record InputCommand(
        float moveX,
        float moveY,
        float aimWorldX,
        float aimWorldY,
        boolean fire,
        boolean dash,
        boolean pauseToggle,
        boolean weapon1,
        boolean weapon2,
        boolean weapon3,
        boolean interact
) {
    public static InputCommand idle(float aimX, float aimY) {
        return new InputCommand(0, 0, aimX, aimY, false, false, false, false, false, false, false);
    }
}
