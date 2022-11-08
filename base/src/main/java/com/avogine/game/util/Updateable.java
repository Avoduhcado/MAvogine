package com.avogine.game.util;

/**
 * Interface for marking an object as something that should be updated once per game loop.
 */
public interface Updateable extends Registerable {

	/**
	 * This will be called once per game loop and should be where any recurring logic is performed.
	 * @param gameState A snapshot of relevant data for the current game loop.
	 */
	public void onUpdate(GameState gameState);
	
}
