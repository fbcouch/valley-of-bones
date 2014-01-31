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
package com.ahsgaming.valleyofbones;

import com.badlogic.gdx.graphics.Color;

/**
 * @author jami
 *
 */
public class NetPlayer extends Player {

	boolean isReady = false;
	
	/**
	 * @param id
	 * @param name
	 * @param color
	 */
	public NetPlayer(int id, String name, Color color) {
		super(id, name, color);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param id
	 * @param color
	 */
	public NetPlayer(int id, Color color) {
		super(id, color);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the isReady
	 */
	public boolean isReady() {
		return isReady;
	}

	/**
	 * @param isReady the isReady to set
	 */
	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}

	
}
