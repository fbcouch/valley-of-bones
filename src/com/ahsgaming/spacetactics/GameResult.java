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
package com.ahsgaming.spacetactics;

/**
 * @author jami
 * GameResult class is used to pass "game results" around - ie: who won/lost
 */
public class GameResult {
	public String LOG = "GameResult";
	
	public int[] winners;
	public int[] losers;
	public int winningTeam;
	
	/**
	 * creates an empty GameResult
	 */
	public GameResult() {
		winners = new int[0];
		losers = new int[0];
		winningTeam = -1;
	}
	
	/**
	 * creates a new GameResult with the given parameters
	 * @param winners
	 * @param losers
	 * @param winningTeam
	 */
	public GameResult(int[] winners, int[] losers, int winningTeam) {
		this.winners = winners;
		this.losers = losers;
		this.winningTeam = winningTeam;
	}

}
