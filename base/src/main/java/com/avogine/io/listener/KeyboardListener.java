package com.avogine.io.listener;

import com.avogine.io.event.KeyboardEvent;

/**
 * 
 */
public interface KeyboardListener extends InputListener {

	/**
	 * @param event
	 */
	public void keyPressed(KeyboardEvent event);
	
	/**
	 * Fired when a keyboard key is released
	 * @param event
	 */
	public void keyReleased(KeyboardEvent event);
	
	/**
	 * Fired when a key is first pressed only once until it's released.
	 * @param event
	 */
	public void keyTyped(KeyboardEvent event);
}
