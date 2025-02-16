package com.avogine.io.event;

import com.avogine.io.Window;

/**
 *
 */
public abstract sealed class InputEvent extends AvoEvent permits MouseEvent, KeyEvent {
	
	protected long window;
	
	/**
	 * @return  the ID of the GLFW {@link Window} that captured this event.
	 */
	public long window() {
		return window;
	}
	
}
