package com.avogine.core.system.event;

public class KeyboardEvent extends Event {

	private final int type;
	private int key;
	
	private long window;
	
	public KeyboardEvent(int type, int key, long window) {
		this.type = type;
		this.key = key;
		this.window = window;
	}
	
	public int getType() {
		return type;
	}
	
	public int getKey() {
		return key;
	}
	
	public long getWindow() {
		return window;
	}
}
