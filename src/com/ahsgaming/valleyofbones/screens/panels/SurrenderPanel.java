package com.ahsgaming.valleyofbones.screens.panels;

import com.ahsgaming.valleyofbones.VOBGame;
import com.ahsgaming.valleyofbones.network.Surrender;
import com.ahsgaming.valleyofbones.screens.LevelScreen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created with IntelliJ IDEA.
 * User: jami
 * Date: 5/16/13
 * Time: 4:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class SurrenderPanel extends Panel {
    public static final String LOG = "SurrenderPanel";

    TextButton btnSurrender;

    public SurrenderPanel(VOBGame vobgame, LevelScreen levelScreen, Skin skin) {
        super(vobgame, levelScreen, "flying-flag", skin);

        btnSurrender = new TextButton("Surrender", skin, "large-cancel");
        btnSurrender.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                Surrender s = new Surrender();
                s.owner = game.getPlayer().getPlayerId();
                game.sendCommand(s);
            }
        });
    }

    @Override
    public void rebuild() {
        super.rebuild();

        addActor(btnSurrender);
        btnSurrender.setPosition(0, 0);

        icon.setPosition(btnSurrender.getRight(), 0);

        setSize(icon.getRight(), icon.getTop());

        if (expanded) setPosition(0, getY()); else setPosition(-1 * icon.getX(), getY());
    }
}
