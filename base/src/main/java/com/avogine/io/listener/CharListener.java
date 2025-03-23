package com.avogine.io.listener;

import com.avogine.io.event.CharEvent;

/**
 *
 */
public interface CharListener extends InputListener {

	/**
	 * @param event
	 */
	public void charTyped(CharEvent event);
	
}
