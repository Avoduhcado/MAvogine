package com.avogine.io.event;

/**
 *
 * @param type
 * @param button
 * @param mouseX
 * @param mouseY
 * @param window
 */
public record MouseClickEvent(int type, int button, float mouseX, float mouseY, long window) implements MouseEvent {
	
}
