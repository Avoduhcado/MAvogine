package com.avogine.io.listener;

import com.avogine.io.event.MouseEvent.*;

/**
 * A listener that fires when a mouse button has been pressed or released.
 */
public interface MouseButtonListener extends InputListener {

	/**
	 * @param event
	 * @return the event
	 */
	public MouseButtonEvent mouseClicked(MouseClickedEvent event);
	
	/**
	 * @param event
	 * @return the event
	 */
	public MouseButtonEvent mousePressed(MousePressedEvent event);
	
	/**
	 * @param event
	 * @return the event
	 */
	public MouseButtonEvent mouseReleased(MouseReleasedEvent event);
	
}
