/**
 * Copyright 2012 Jami Couch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This project uses:
 * 
 * LibGDX
 * Copyright 2011 see LibGDX AUTHORS file
 * Licensed under Apache License, Version 2.0 (see above).
 * 
 */
package com.ahsgaming.valleyofbones.units;

import java.util.ArrayList;

import com.ahsgaming.valleyofbones.*;
import com.ahsgaming.valleyofbones.ai.UnitFSM;
import com.ahsgaming.valleyofbones.network.Command;
import com.ahsgaming.valleyofbones.screens.LevelScreen;
import com.ahsgaming.valleyofbones.units.Prototypes.JsonProto;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * @author jami
 *
 */
public class Unit {
	public String LOG = "Unit";

	JsonProto proto;
    UnitData data;
    UnitView view;
    UnitFSM fsm;
    Player owner;
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public JsonProto getProto() {
        return proto;
    }

    public UnitData getData() {
        return data;
    }

    public UnitView getView() {
        return view;
    }

    public UnitFSM getFsm() {
        return fsm;
    }

    public Player getOwner() {
        return owner;
    }

    public static Unit createUnit(int id, String protoId, Player owner) {
        Unit unit = new Unit();

        unit.id = id;
        unit.owner = owner;
        unit.proto = Prototypes.getProto((owner != null ? owner.getRace() : "terran"), protoId);
        unit.data = UnitData.createUnitData(unit.proto);
        unit.view = UnitView.createUnitView(unit);


        if (unit.data.buildTime > 0) {
            unit.data.building = true;
            unit.data.buildTimeLeft = unit.data.buildTime;
        }

        return unit;
    }

}
