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
package com.ahsgaming.valleyofbones.screens;

import com.ahsgaming.valleyofbones.VOBGame;
import com.ahsgaming.valleyofbones.network.KryoCommon;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * @author jami
 *
 */
public class ServerScreen extends AbstractScreen {

	/**
	 * @param game
	 */
	public ServerScreen(VOBGame game) {
		super(game);
		
	}

	/* (non-Javadoc)
	 * @see com.ahsgaming.spacetactics.screens.AbstractScreen#show()
	 */
	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
	}

	/* (non-Javadoc)
	 * @see com.ahsgaming.spacetactics.screens.AbstractScreen#resize(int, int)
	 */
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		super.resize(width, height);
		
		Table table = new Table(getSkin());
		table.setFillParent(true);
		
		table.add("Valley of Bones");
		table.row();
		table.add("Standalone server");
		table.row();
		table.add(String.format("Port %s", KryoCommon.tcpPort));
		table.row();
		table.add("Close window to exit");
		
		stage.addActor(table);
	}

	/* (non-Javadoc)
	 * @see com.ahsgaming.spacetactics.screens.AbstractScreen#render(float)
	 */
	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		super.render(delta);
	}
	
	

}
