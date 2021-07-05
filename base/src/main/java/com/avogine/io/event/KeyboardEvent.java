package com.avogine.io.event;

import org.lwjgl.glfw.*;

/**
 * @param type the type of event, one of {@link GLFW#GLFW_PRESS}, {@link GLFW#GLFW_RELEASE} or {@link #KEY_TYPED}
 * @param key the key that triggered the event
 * @param window the window ID the key was triggered from
 *
 */
public record KeyboardEvent(int type, int key, long window) implements Event {

	/**
	 * Constant value for events where a key was just pressed.
	 * </p>
	 * This will only fire one event regardless of how long a key is held down for.
	 */
	public static final int KEY_TYPED = 3;
	
}
