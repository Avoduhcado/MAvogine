package com.avogine.game.util;

import com.avogine.game.HotGame;

/**
 * 
 */
public sealed interface GameListener permits Updateable, Renderable, Cleanupable {

	/**
	 * Run any additional configurations that may need to occur prior to a {@link GameListener}'s first run.
	 * @param game The {@link HotGame} to register this interface to.
	 */
	public void onRegister(HotGame game);
	
}
