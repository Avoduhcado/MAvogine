package com.avogine.io.event;

import com.avogine.io.Window;

/**
 * Base interface for {@link AvoEvent}s fired by user input.
 */
public sealed interface InputEvent extends AvoEvent permits MouseEvent, KeyEvent, CharEvent {
	
	/**
	 * @return  the ID of the GLFW {@link Window} that captured this event.
	 */
	public long window();
	
}
