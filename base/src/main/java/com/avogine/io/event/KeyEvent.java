package com.avogine.io.event;

/**
 * 
 */
public non-sealed class KeyEvent extends InputEvent {
	
	public static final int KEY_PRESSED = 101;
	
	public static final int KEY_RELEASED = 102;

	/**
	 * Constant value for events where a key was just pressed.
	 * </p>
	 * This will only fire one event regardless of how long a key is held down for.
	 */
	public static final int KEY_TYPED = 103;
	
	public final int key;
	
	public final int codepoint;
	
	/**
	 * @param id the type of event, one of {@link #KEY_PRESSED}, {@link #KEY_RELEASED} or {@link #KEY_TYPED}
	 * @param key the key that triggered the event
	 * @param codepoint The character produced by this event.
	 * @param window the window ID the key was triggered from
	 */
	public KeyEvent(int id, int key, int codepoint, long window) {
		this.id = id;
		this.key = key;
		this.codepoint = codepoint;
		this.window = window;
	}
	
	/**
	 * @return the key
	 */
	public int key() {
		return key;
	}
	
	/**
	 * @return the unicode codepoint
	 */
	public int codepoint() {
		return codepoint;
	}

}
