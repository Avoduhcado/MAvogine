package com.avogine.io.listener;

import com.avogine.io.event.KeyEvent;
import com.avogine.io.event.KeyEvent.*;

/**
 * 
 */
public interface KeyListener extends InputListener {

	/**
	 * @param event
	 * @return the event
	 */
	public KeyEvent keyPressed(KeyPressedEvent event);
	
	/**
	 * Fired when a keyboard key is released
	 * @param event
	 * @return the event
	 */
	public KeyEvent keyReleased(KeyReleasedEvent event);
	
	/**
	 * Fired when a key is first pressed only once until it's released.
	 * @param event
	 * @return the event
	 */
	public KeyEvent keyTyped(KeyTypedEvent event);
}
