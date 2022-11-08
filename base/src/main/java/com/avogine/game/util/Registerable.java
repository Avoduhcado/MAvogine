package com.avogine.game.util;

import com.avogine.game.Game;

/**
 * TODO
 * Make sure to call {@link #register(Game)} after instantiating your object so that it will
 * be picked up by the {@code Game} and updated automatically.
 */
public interface Registerable {

	/**
	 * Run any additional configurations that may need to occur prior to a {@link Registerable}'s first run.
	 */
	public void onRegister();
	
}
