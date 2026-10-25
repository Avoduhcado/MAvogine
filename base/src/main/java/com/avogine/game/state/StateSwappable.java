package com.avogine.game.state;

import com.avogine.game.util.Stateful;
import com.avogine.io.Window;

/**
 * Interface for enabling {@link GameState} changes.
 * @param <T>
 * @deprecated Use {@link Stateful} instead. 
 */
@Deprecated(since = "0.1.4", forRemoval = true)
public interface StateSwappable<T extends GameState<?, ?>> {

	/**
	 * @return
	 */
	public T currentState();
	
	/**
	 * @return
	 */
	public boolean hasQueuedGameState();
	
	/**
	 * @param window
	 */
	public void swapGameState(Window window);
	
}
