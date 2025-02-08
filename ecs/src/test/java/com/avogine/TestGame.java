package com.avogine;

import com.avogine.ecs.TestECSGameState;
import com.avogine.game.HotSwapGame;
import com.avogine.game.state.GameState;
import com.avogine.io.Window;

/**
 *
 */
public class TestGame extends HotSwapGame {

	/**
	 * @param gameState 
	 */
	public TestGame(TestECSGameState gameState) {
		super(gameState);
	}

	@Override
	public void init(Window window) {
		// Not implemented
	}

	@Override
	public boolean hasQueuedGameState() {
		return false;
	}

	@Override
	public GameState<?, ?> currentState() {
		return null;
	}

}
