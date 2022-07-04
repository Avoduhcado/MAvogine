package com.avogine.io.event;

import org.lwjgl.glfw.*;

/**
 *
 * @param type The button action as specified by {@link GLFWMouseButtonCallbackI#invoke(long, int, int, int)}
 * @param button The mouse button firing the action
 * @param mouseX
 * @param mouseY
 * @param window
 */
public record MouseClickEvent(int type, int button, float mouseX, float mouseY, long window) implements MouseEvent {
	
}
