package com.avogine.io.listener;

import com.avogine.io.event.MouseEvent.MouseWheelEvent;

/**
 * Respond to mouse wheel events.
 */
public interface MouseWheelListener extends InputListener {

	/**
	 * @param event
	 */
	public void mouseWheelMoved(MouseWheelEvent event);
	
}
