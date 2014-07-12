package com.ahsgaming.valleyofbones;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;

/**
 * Created by jami on 7/12/14.
 */
public class Main {
    public static void main(String[] args) {
        HeadlessApplicationConfiguration cfg = new HeadlessApplicationConfiguration();
        new HeadlessApplication(new VOBServer(), cfg);
    }
}
