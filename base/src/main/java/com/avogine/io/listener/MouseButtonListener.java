package com.avogine.io.listener;

import com.avogine.io.event.MouseEvent;

/**
 * A listener that fires when a mouse button has been pressed or released.
 */
public interface MouseButtonListener extends InputListener {

	/**
	 * @param event
	 */
	public void mouseClicked(MouseEvent event);
	
	/**
	 * @param event
	 */
	public void mousePressed(MouseEvent event);
	
	/**
	 * @param event
	 */
	public void mouseReleased(MouseEvent event);
	
}
