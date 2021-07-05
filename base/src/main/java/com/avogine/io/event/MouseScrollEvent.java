package com.avogine.io.event;

/**
 *
 * @param xOffset
 * @param yOffset
 * @param window
 */
public record MouseScrollEvent(float xOffset, float yOffset, long window) implements MouseEvent {

	/**
	 * @param xOffset
	 * @param yOffset
	 * @param window
	 */
	public MouseScrollEvent(double xOffset, double yOffset, long window) {
		this((float) xOffset, (float) yOffset, window);
	}
	
}
