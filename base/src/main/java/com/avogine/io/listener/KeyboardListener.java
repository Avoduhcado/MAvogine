package com.avogine.io.listener;

import com.avogine.io.event.KeyboardEvent;

/**
 * TODO keyReleased method and maybe a keyTyped?
 */
public interface KeyboardListener extends InputListener {

	/**
	 * @param event
	 */
	public void keyPressed(KeyboardEvent event);
	
}
