package com.avogine.io.listener;

import com.avogine.io.event.MouseEvent.MouseButtonEvent;

/**
 * A listener that fires when a mouse button has been pressed or released.
 */
public interface MouseButtonListener extends InputListener {

	/**
	 * @param event
	 */
	public void mouseClicked(MouseButtonEvent event);
	
	/**
	 * @param event
	 */
	public void mousePressed(MouseButtonEvent event);
	
	/**
	 * @param event
	 */
	public void mouseReleased(MouseButtonEvent event);
	
}
