package com.avogine.io.listener;

import com.avogine.io.event.*;

/**
 * Respond to mouse wheel events.
 */
public interface MouseScrollListener extends InputListener {

	/**
	 * @param event
	 */
	public void mouseScrolled(MouseScrollEvent event);
	
}
