/**
 * 
 */
package com.avogine.game;

import com.avogine.io.Window;

/**
 *
 */
public interface Game {

	/**
	 * Initialize all relevant game logic to start the game loop.
	 */
	public void init(Window window);
	
	/**
	 * Update any relevant entities or systems at a fixed time step determined by {@code interval}.
	 * @param interval The time elapsed between updates in seconds
	 */
	public void update(float interval);
	
	/**
	 * 
	 */
	public void render();
	
	/**
	 * 
	 */
	public void cleanup();
	
}
