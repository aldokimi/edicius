package com.edicius.input;

public record InputCommand(
        float moveX,
        float moveY,
        float aimWorldX,
        float aimWorldY,
        boolean fire,
        boolean dash,
        boolean pauseToggle
) {
}
