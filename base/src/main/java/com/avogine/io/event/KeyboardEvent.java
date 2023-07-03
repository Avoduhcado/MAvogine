package com.avogine.io.event;

import java.util.concurrent.atomic.AtomicBoolean;

import org.lwjgl.glfw.GLFW;

/**
 * @param type the type of event, one of {@link GLFW#GLFW_PRESS}, {@link GLFW#GLFW_RELEASE} or {@link #KEY_TYPED}
 * @param key the key that triggered the event
 * @param window the window ID the key was triggered from
 * @param stopped
 *
 */
public record KeyboardEvent(int type, int key, long window, AtomicBoolean stopped) implements Event {
	
	/**
	 * @param type
	 * @param key
	 * @param window
	 */
	public KeyboardEvent(int type, int key, long window) {
		this(type, key, window, new AtomicBoolean(false));
	}

	/**
	 * Constant value for events where a key was just pressed.
	 * </p>
	 * This will only fire one event regardless of how long a key is held down for.
	 */
	public static final int KEY_TYPED = 3;

	@Override
	public void consume() {
		stopped.set(true);
	}
	
	public boolean isConsumed() {
		return stopped.get();
	}

}
