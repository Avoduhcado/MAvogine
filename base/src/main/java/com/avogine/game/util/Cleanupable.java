package com.avogine.game.util;

import com.avogine.game.*;

/**
 * 
 */
public interface Cleanupable {

	/**
	 * 
	 */
	public void onCleanup();
	
	/**
	 * Register this {@link Cleanupable} with the given {@link Game}.
	 * @param game The {@code Game} to register this {@code Cleanupable} with.
	 */
	public default void register(Game game) {
		game.addCleanupable(this);
	}
	
}
