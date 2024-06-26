package com.avogine.io.listener;

import com.avogine.io.event.MouseEvent;

/**
 * Respond to the mouse moving on the screen.
 */
public interface MouseMotionListener extends InputListener {
	

	/**
	 * @param event
	 */
	public void mouseMoved(MouseEvent event);
	
	/**
	 * @param event
	 */
	public void mouseDragged(MouseEvent event);
	
}
