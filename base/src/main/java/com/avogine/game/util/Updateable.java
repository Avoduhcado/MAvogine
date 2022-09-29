package com.avogine.game.util;

import com.avogine.game.*;

/**
 * Interface for marking an object as something that should be updated once per game loop.
 * <p>
 * Make sure to call {@link #register(Game)} after instantiating your object so that it will
 * be picked up the {@code Game} and updated automatically.
 */
public interface Updateable {

	/**
	 * This will be called once per game loop and should be where any recurring logic is performed.
	 * @param gameState A snapshot of relevant data for the current game loop.
	 */
	public void onUpdate(GameState gameState);
	
	/**
	 * Register this {@link Updateable} with the given {@link Game}.
	 * @param game The {@code Game} to register this {@code Updateable} with.
	 */
	public default void register(Game game) {
		game.addUpdateable(this);
	}
	
}
