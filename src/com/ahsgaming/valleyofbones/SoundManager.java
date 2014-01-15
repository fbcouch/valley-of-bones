package com.ahsgaming.valleyofbones;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * Created on 12/29/13 by jami
 * ahsgaming.com
 */
public class SoundManager {
    public static final String LOG = "SoundManager";

    float volume = 0;

    ObjectMap<String, Sound> soundMap;

    public SoundManager() {
        soundMap = new ObjectMap<String, Sound>();
    }

    public Sound getSound(String sound) {
        if (!soundMap.containsKey(sound)) {
            FileHandle file = Gdx.files.internal("sfx/" + sound + ".ogg");
            if (!file.exists()) return null;
            soundMap.put(sound, Gdx.audio.newSound(file));
        }
        return soundMap.get(sound);
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public void dispose() {
        for (Sound s: soundMap.values()) {
            s.dispose();
        }
    }
}
