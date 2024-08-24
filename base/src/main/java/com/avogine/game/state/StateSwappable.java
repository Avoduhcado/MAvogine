package com.avogine.game.state;

import com.avogine.io.Window;

/**
 * Interface for enabling {@link GameState} changes.
 * @param <T> 
 */
public interface StateSwappable<T extends GameState<?, ?>> {

	/**
	 * @param gameStateClass
	 */
	public void queueGameState(Class<T> gameStateClass);
	
	/**
	 * @param window
	 */
	public void swapGameState(Window window);
	
}
