package com.avogine.io.event;

public class MouseMotionEvent extends MouseEvent {

	private float xPosition;
	private float yPosition;
	
	private float xDelta;
	private float yDelta;
	
	public MouseMotionEvent(long window, float xPosition, float yPosition, float xDelta, float yDelta) {
		this.window = window;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.xDelta = xDelta;
		this.yDelta = yDelta;
	}
	
	public MouseMotionEvent(long window, double xPosition, double yPosition, double xDelta, double yDelta) {
		this(window, (float) xPosition, (float) yPosition, (float) xDelta, (float) yDelta);
	}
	
	public float getXPosition() {
		return xPosition;
	}
	
	public float getYPosition() {
		return yPosition;
	}

	public float getXDelta() {
		return xDelta;
	}
	
	public float getYDelta() {
		return yDelta;
	}
}
