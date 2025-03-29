package com.avogine.game.util;

import com.avogine.io.Window;

/**
 * Interface for game components that need additional initialization that must occur on the main thread.
 */
public interface Registerable {

	/**
	 * @param window 
	 */
	public void init(Window window);
	
}
