package com.avogine.game.util;

/**
 * 
 */
public sealed interface GameListener permits Updateable, Renderable, Cleanupable {

	/**
	 * Run any additional configurations that may need to occur prior to a {@link GameListener}'s first run.
	 * @param game The {@link RegisterableGame} to register this listener to.
	 */
	public void onRegister(RegisterableGame game);
	
}
