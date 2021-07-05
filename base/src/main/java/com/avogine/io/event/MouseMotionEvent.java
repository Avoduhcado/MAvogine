package com.avogine.io.event;

/**
 *
 * @param xPosition
 * @param yPosition
 * @param xDelta
 * @param yDelta
 * @param window
 */
public record MouseMotionEvent(float xPosition, float yPosition, float xDelta, float yDelta, long window) implements MouseEvent {

	/**
	 * @param xPosition
	 * @param yPosition
	 * @param xDelta
	 * @param yDelta
	 * @param window
	 */
	public MouseMotionEvent(double xPosition, double yPosition, double xDelta, double yDelta, long window) {
		this((float) xPosition, (float) yPosition, (float) xDelta, (float) yDelta, window);
	}
	
}
