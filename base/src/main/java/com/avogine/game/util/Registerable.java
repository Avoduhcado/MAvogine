package com.avogine.game.util;

/**
 * 
 */
public sealed interface Registerable permits Updateable, Renderable, Cleanupable {

	/**
	 * Run any additional configurations that may need to occur prior to a {@link Registerable}'s first run.
	 */
	public void onRegister();
	
}
