package com.avogine.io.event;

/**
 *
 * @param xOffset
 * @param yOffset
 * @param window
 */
public class MouseWheelEvent extends MouseEvent {

	public final double xOffset;
	
	public final double yOffset;
	
	/**
	 * @param xOffset
	 * @param yOffset
	 * @param window
	 */
	public MouseWheelEvent(double xOffset, double yOffset, long window) {
		super(MouseEvent.MOUSE_WHEEL, -1, 0, 0f, 0f, window);
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
	
	public double xOffset() {
		return xOffset;
	}
	
	public double yOffset() {
		return yOffset;
	}
}
