package com.edicius.gfx;

import com.badlogic.gdx.graphics.Texture;

public class UnitSkin {

    public final Texture stand;
    public final Texture armed;

    public UnitSkin(Texture stand, Texture armed) {
        this.stand = stand;
        this.armed = armed != null ? armed : stand;
    }

    public void dispose() {
        if (stand != null) stand.dispose();
        if (armed != null && armed != stand) armed.dispose();
    }
}
