package com.avogine.io.event;

/**
 *
 */
public interface MouseEvent extends Event {

	/** 
	 * @return the ID of the GLFW window that captured this event.
	 */
	public long window();

}
