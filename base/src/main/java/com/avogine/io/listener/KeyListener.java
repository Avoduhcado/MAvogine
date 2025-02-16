package com.avogine.io.listener;

import com.avogine.io.event.KeyEvent;

/**
 * 
 */
public interface KeyListener extends InputListener {

	/**
	 * @param event
	 */
	public void keyPressed(KeyEvent event);
	
	/**
	 * Fired when a keyboard key is released
	 * @param event
	 */
	public void keyReleased(KeyEvent event);
	
	/**
	 * Fired when a key is first pressed only once until it's released.
	 * @param event
	 */
	public void keyTyped(KeyEvent event);
}
