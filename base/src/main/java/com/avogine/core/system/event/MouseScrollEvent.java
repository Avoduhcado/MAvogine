package com.avogine.core.system.event;

public class MouseScrollEvent extends MouseEvent {

	private float xOffset;
	private float yOffset;
	
	public MouseScrollEvent(long window, float xOffset, float yOffset) {
		this.window = window;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	public MouseScrollEvent(long window, double xOffset, double yOffset) {
		this(window, (float) xOffset, (float) yOffset);
	}
	
	public float getxOffset() {
		return xOffset;
	}
	
	public float getyOffset() {
		return yOffset;
	}
	
}
