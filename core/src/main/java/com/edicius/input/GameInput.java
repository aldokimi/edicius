package com.edicius.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

/**
 * Twin-stick input. Movement is camera-relative so arrow/WASD match the iso view.
 */
public class GameInput {

    private final Vector3 forward = new Vector3();
    private final Vector3 right = new Vector3();

    public InputCommand poll(Camera camera) {
        float sx = 0f;
        float sy = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            sx -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            sx += 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            sy += 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            sy -= 1f;
        }

        // Screen-space intent → sim XZ using camera facing (Y-up world).
        forward.set(camera.direction.x, 0f, camera.direction.z);
        if (forward.len2() < 1e-6f) {
            forward.set(0f, 0f, -1f);
        } else {
            forward.nor();
        }
        right.set(forward).crs(0f, 1f, 0f).nor(); // forward × up → camera right

        float mx = right.x * sx + forward.x * sy;
        float my = right.z * sx + forward.z * sy;
        if (mx != 0f || my != 0f) {
            float len = (float) Math.sqrt(mx * mx + my * my);
            mx /= len;
            my /= len;
        }

        float aimX = 0f;
        float aimY = 0f;
        Ray ray = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
        // Intersect ground plane Y = 0 (sim x/z map to world x/z).
        if (Math.abs(ray.direction.y) > 0.0001f) {
            float t = -ray.origin.y / ray.direction.y;
            if (t > 0f) {
                aimX = ray.origin.x + ray.direction.x * t;
                aimY = ray.origin.z + ray.direction.z * t;
            }
        }

        return new InputCommand(
                mx,
                my,
                aimX,
                aimY,
                Gdx.input.isButtonPressed(Input.Buttons.LEFT),
                Gdx.input.isKeyJustPressed(Input.Keys.SPACE),
                Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.P),
                Gdx.input.isKeyJustPressed(Input.Keys.NUM_1),
                Gdx.input.isKeyJustPressed(Input.Keys.NUM_2),
                Gdx.input.isKeyJustPressed(Input.Keys.NUM_3),
                Gdx.input.isKeyJustPressed(Input.Keys.E)
        );
    }
}
