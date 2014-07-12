package com.ahsgaming.valleyofbones;

import com.ahsgaming.valleyofbones.TextureManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * Created by jami on 7/12/14.
 */
public class ServerTextureManager extends TextureManager {
    @Override
    public TextureRegion getTexture(String file) {
        return null;
    }

    @Override
    public TextureRegion loadTextureRegion(String file, int x, int y, int w, int h) {
        return null;
    }

    @Override
    public Sprite getSpriteFromAtlas(String atlas, String name, int id) {
        return null;
    }

    @Override
    public Sprite getSpriteFromAtlas(String atlas, String name) {
        return null;
    }

    @Override
    public Array<Sprite> getSpritesFromAtlas(String atlas, String name) {
        return new Array<Sprite>();
    }
}
