package com.avogine.io.listener;

import com.avogine.io.event.*;

/**
 * Respond to the mouse moving on the screen.
 */
public interface MouseMotionListener extends InputListener {

	/**
	 * @param event
	 */
	public void mouseMoved(MouseMotionEvent event);
	
}
