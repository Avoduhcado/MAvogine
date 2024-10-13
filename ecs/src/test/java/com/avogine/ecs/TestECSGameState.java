package com.avogine.ecs;

import com.avogine.game.state.GameState;
import com.avogine.io.Window;

/**
 *
 */
public class TestECSGameState extends GameState<TestECSScene, TestECSRender> {

	/**
	 * 
	 */
	public TestECSGameState() {
		scene = new TestECSScene();
	}
	
	@Override
	public void init(Window window) {
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

}
