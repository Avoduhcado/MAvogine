package com.avogine.game.util;

import com.avogine.game.state.GameState;
import com.avogine.io.Window;

/**
 * @param <T> 
 */
public interface Stateful<T extends GameState<?, ?>> {

	/**
	 * @return the current state
	 */
	public T getState();
	
	/**
	 * @param state
	 */
	public void setState(T state);
	
	/**
	 * @param newState
	 * @param window
	 */
	public default void transitionTo(T newState, Window window) {
		if (getState() != null) {
			getState().cleanup();
		}
		
		setState(newState);
		
		getState().init(window);
	}
	
}
