package com.avogine.game.util;

/**
 * 
 */
public non-sealed interface Cleanupable extends GameListener {

	/**
	 * Handle freeing up any memory that was manually allocated.
	 * </p>
	 * All manual memory allocations should be made in the corresponding {@link #onRegister(com.avogine.game.Game)}
	 * method so that when the system shuts down and this is called that memory can be 
	 * freed properly. 
	 */
	public void onCleanup();
	
}
