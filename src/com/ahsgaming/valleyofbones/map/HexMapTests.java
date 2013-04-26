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
package com.ahsgaming.valleyofbones.map;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * @author jami
 *
 */
public class HexMapTests {

	public static class DistTestCase {
		int x1, y1, x2, y2, d;
		
		public DistTestCase(int x1, int y1, int x2, int y2, int dist) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
			this.d = dist;
		}
	}
	
	/**
	 * Test method for {@link com.ahsgaming.valleyofbones.map.HexMap#getMapDist(com.badlogic.gdx.math.Vector2, com.badlogic.gdx.math.Vector2)}.
	 */
	@Test
	public void testGetMapDist() {
		HexMap map = new HexMap(10, 10, 2, 3);
		
		Array<DistTestCase> cases = new Array<DistTestCase>();
		
		// d = 1
		cases.add(new DistTestCase(2, 2, 3, 2, 1));
		cases.add(new DistTestCase(2, 2, 2, 3, 1));
		cases.add(new DistTestCase(2, 2, 1, 3, 1));
		cases.add(new DistTestCase(2, 2, 1, 2, 1));
		cases.add(new DistTestCase(2, 2, 1, 1, 1));
		cases.add(new DistTestCase(2, 2, 2, 1, 1));
		
		cases.add(new DistTestCase(3, 3, 4, 3, 1));
		cases.add(new DistTestCase(3, 3, 4, 4, 1));
		cases.add(new DistTestCase(3, 3, 3, 4, 1));
		cases.add(new DistTestCase(3, 3, 2, 3, 1));
		cases.add(new DistTestCase(3, 3, 3, 2, 1));
		cases.add(new DistTestCase(3, 3, 4, 2, 1));
		
		// d = 2
		cases.add(new DistTestCase(2, 2, 4, 2, 2));
		cases.add(new DistTestCase(2, 2, 3, 3, 2));
		cases.add(new DistTestCase(2, 2, 3, 4, 2));
		cases.add(new DistTestCase(2, 2, 2, 4, 2));
		cases.add(new DistTestCase(2, 2, 1, 4, 2));
		cases.add(new DistTestCase(2, 2, 0, 3, 2));
		cases.add(new DistTestCase(2, 2, 0, 2, 2));
		cases.add(new DistTestCase(2, 2, 0, 1, 2));
		cases.add(new DistTestCase(2, 2, 1, 0, 2));
		cases.add(new DistTestCase(2, 2, 2, 0, 2));
		cases.add(new DistTestCase(2, 2, 3, 0, 2));
		cases.add(new DistTestCase(2, 2, 3, 1, 2));
		
		cases.add(new DistTestCase(3, 3, 5, 3, 2));
		cases.add(new DistTestCase(3, 3, 5, 4, 2));
		cases.add(new DistTestCase(3, 3, 4, 5, 2));
		cases.add(new DistTestCase(3, 3, 3, 5, 2));
		cases.add(new DistTestCase(3, 3, 2, 5, 2));
		cases.add(new DistTestCase(3, 3, 2, 4, 2));
		cases.add(new DistTestCase(3, 3, 1, 3, 2));
		cases.add(new DistTestCase(3, 3, 2, 2, 2));
		cases.add(new DistTestCase(3, 3, 2, 1, 2));
		cases.add(new DistTestCase(3, 3, 3, 1, 2));
		cases.add(new DistTestCase(3, 3, 4, 1, 2));
		cases.add(new DistTestCase(3, 3, 5, 2, 2));
		
		// d = 3 (just a few)
		cases.add(new DistTestCase(0, 0, 3, 0, 3));
		cases.add(new DistTestCase(3, 0, 0, 0, 3));
		
		cases.add(new DistTestCase(0, 0, 0, 3, 3));
		cases.add(new DistTestCase(0, 3, 0, 0, 3));
		
		cases.add(new DistTestCase(0, 0, 2, 2, 3));
		cases.add(new DistTestCase(2, 2, 0, 0, 3));
		
		// other
		cases.add(new DistTestCase(0, 0, 3, 3, 5));
		cases.add(new DistTestCase(3, 3, 0, 0, 5));
		
		cases.add(new DistTestCase(0, 1, 4, 4, 5));
		cases.add(new DistTestCase(4, 4, 0, 1, 5));
		
		for (DistTestCase dtc: cases) {
			assertEquals(String.format("getMapDist (%d, %d) ---> (%d, %d)", dtc.x1, dtc.y1, dtc.x2, dtc.y2),
						 dtc.d,
						 map.getMapDist(new Vector2(dtc.x1, dtc.y1), new Vector2(dtc.x2, dtc.y2)));
		}
		
	}

}
