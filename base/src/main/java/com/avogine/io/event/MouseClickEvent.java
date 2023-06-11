package com.avogine.io.event;

import java.util.concurrent.atomic.AtomicBoolean;

import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

/**
 *
 * @param type The button action as specified by {@link GLFWMouseButtonCallbackI#invoke(long, int, int, int)}
 * @param button The mouse button firing the action
 * @param mouseX
 * @param mouseY
 * @param window
 * @param stopped
 */
public record MouseClickEvent(int type, int button, float mouseX, float mouseY, long window, AtomicBoolean stopped) implements MouseEvent {

	public MouseClickEvent(int type, int button, float mouseX, float mouseY, long window) {
		this(type, button, mouseX, mouseY, window, new AtomicBoolean(false));
	}
	
	@Override
	public void consume() {
		stopped.set(true);
	}

	@Override
	public boolean isConsumed() {
		return stopped.get();
	}
	
}
