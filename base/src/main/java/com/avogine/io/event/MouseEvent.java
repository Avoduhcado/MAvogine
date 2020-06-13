package com.avogine.io.event;

public abstract class MouseEvent extends Event {

	/** 
	 * <p>The ID of the GLFW window that captured this event.
	 */
	protected long window;

	public long getWindow() {
		return window;
	}

}
