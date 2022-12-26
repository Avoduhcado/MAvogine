package com.avogine.game.util;

import com.avogine.game.Game;

/**
 * 
 */
public sealed interface Registerable permits Updateable, Renderable, Cleanupable {

	/**
	 * Run any additional configurations that may need to occur prior to a {@link Registerable}'s first run.
	 * @param game The {@link Game} to register this interface to.
	 */
	public void onRegister(Game game);
	
}
