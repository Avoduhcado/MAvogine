package com.avogine.io.listener;

import com.avogine.io.event.MouseEvent.*;

/**
 * Respond to the mouse moving on the screen.
 */
public interface MouseMotionListener extends InputListener {
	

	/**
	 * @param event
	 * @return the event
	 */
	public MouseMotionEvent mouseMoved(MouseMovedEvent event);
	
	/**
	 * @param event
	 * @return the event
	 */
	public MouseMotionEvent mouseDragged(MouseDraggedEvent event);
	
}
