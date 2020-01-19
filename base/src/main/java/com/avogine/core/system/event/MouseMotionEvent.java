package com.avogine.core.system.event;

public class MouseMotionEvent extends MouseEvent {

	private float xPosition;
	private float yPosition;
	
	public MouseMotionEvent(long window, float xPosition, float yPosition) {
		this.window = window;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
	}
	
	public MouseMotionEvent(long window, double xPosition, double yPosition) {
		this(window, (float) xPosition, (float) yPosition);
	}
	
	public float getXPosition() {
		return xPosition;
	}
	
	public float getYPosition() {
		return yPosition;
	}
	
}
