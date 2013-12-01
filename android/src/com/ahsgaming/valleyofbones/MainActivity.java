package com.ahsgaming.valleyofbones;

import android.os.Bundle;
import android.os.Debug;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

/**
 * valley-of-bones
 * (c) 2013 Jami Couch
 * Created on 11/29/13 by jami
 * ahsgaming.com
 */
public class MainActivity extends AndroidApplication {
    @Override
    public void onCreate(Bundle savedIstanceState) {
        super.onCreate(savedIstanceState);

        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = true;

        initialize(new VOBGame(false), cfg);
//        Debug.startMethodTracing("all");
    }

    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
//        Debug.stopMethodTracing();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();    //To change body of overridden methods use File | Settings | File Templates.
//        Debug.stopMethodTracing();
    }
}