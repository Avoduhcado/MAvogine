package com.avogine.io.event;

public class MouseClickEvent extends MouseEvent {

	private final int type;
	private final int button;
	
	private final float mouseX;
	private final float mouseY;
	
	public MouseClickEvent(long window, int button, float mouseX, float mouseY, int type) {
		this.window = window;
		this.button = button;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.type = type;
	}

	public int getButton() {
		return button;
	}
	
	/**
	 * @return the mouseX
	 */
	public float getMouseX() {
		return mouseX;
	}
	
	/**
	 * @return the mouseY
	 */
	public float getMouseY() {
		return mouseY;
	}
	
	public int getType() {
		return type;
	}
	
}
