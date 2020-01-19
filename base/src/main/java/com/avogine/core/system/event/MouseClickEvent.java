package com.avogine.core.system.event;

public class MouseClickEvent extends MouseEvent {

	private final int type;
	private int button;
	
	public MouseClickEvent(long window, int button, int type) {
		this.window = window;
		this.button = button;
		this.type = type;
	}

	public int getButton() {
		return button;
	}
	public int getType() {
		return type;
	}
	
	
}
