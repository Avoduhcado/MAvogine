package com.avogine.game.util;

import com.avogine.game.scene.Scene;

/**
 * Interface for marking an object as something that should be updated once per game loop.
 */
public non-sealed interface Updateable<T extends Scene> extends GameListener {

	/**
	 * This will be called once per game loop and should be where any recurring logic is performed.
	 * @param gameState A snapshot of relevant data for the current game loop.
	 */
	public void onUpdate(GameState<T> gameState);
	
}
