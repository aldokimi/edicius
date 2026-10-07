package com.edicius.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class GameInput {

    private final Vector3 tmp = new Vector3();

    public InputCommand poll(OrthographicCamera camera) {
        float mx = 0f;
        float my = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            mx -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            mx += 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            my += 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            my -= 1f;
        }
        if (mx != 0f || my != 0f) {
            float len = (float) Math.sqrt(mx * mx + my * my);
            mx /= len;
            my /= len;
        }

        tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        camera.unproject(tmp);

        return new InputCommand(
                mx,
                my,
                tmp.x,
                tmp.y,
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
