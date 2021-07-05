package com.avogine.io.listener;

import com.avogine.io.event.MouseClickEvent;

/**
 * A listener that fires when a mouse button has been pressed or released.
 */
public interface MouseClickListener extends InputListener {

	/**
	 * @param event
	 */
	public void mouseClicked(MouseClickEvent event);
	
}
