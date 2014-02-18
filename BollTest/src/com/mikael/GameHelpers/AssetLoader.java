package com.mikael.GameHelpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AssetLoader {

    public static Texture texture;
    public static TextureRegion boll;

    public static void load() {

        texture = new Texture(Gdx.files.internal("data/ball.png"));
        texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

        boll = new TextureRegion(texture, 0, 0, 256, 256);
        boll.flip(false, true);
    }
    
    public static void dispose() {
        // We must dispose of the texture when we are finished.
        texture.dispose();
    }

}
