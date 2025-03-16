package com.avogine.io.event;

import java.util.concurrent.atomic.AtomicBoolean;

import com.avogine.io.listener.KeyListener;

/**
 * Base interface for {@link InputEvent}s fired by key input.
 */
public sealed interface KeyEvent extends InputEvent, ConsumableEvent {
	/**
	 * @return the key
	 */
	public int key();
	
	/**
	 * @return the unicode codepoint
	 */
	public int codepoint();

	/**
	 * KeyEvent where a key is pressed.
	 * <p>
	 * This will continue to fire new events while the key is pressed.
	 * @param window The window ID the key was triggered from.
	 * @param key The key that triggered the event
	 * @param codepoint The character produced by this event.
	 * @param consumed true only if this event has been handled and consumed by a {@link KeyListener}.
	 */
	public record KeyPressedEvent(long window, int key, int codepoint, AtomicBoolean consumed) implements KeyEvent {
		/**
		 * @param window The window ID the key was triggered from.
		 * @param key The key that triggered the event
		 * @param codepoint The character produced by this event.
		 */
		public KeyPressedEvent(long window, int key, int codepoint) {
			this(window, key, codepoint, new AtomicBoolean());
		}
		
		@Override
		public void consume() {
			consumed.set(true);
		}
	}
	
	/**
	 * KeyEvent where a key was released.
	 * @param window The window ID the key was triggered from.
	 * @param key The key that triggered the event
	 * @param codepoint The character produced by this event.
	 * @param consumed true only if this event has been handled and consumed by a {@link KeyListener}.
	 */
	public record KeyReleasedEvent(long window, int key, int codepoint, AtomicBoolean consumed) implements KeyEvent {
		/**
		 * @param window The window ID the key was triggered from.
		 * @param key The key that triggered the event
		 * @param codepoint The character produced by this event.
		 */
		public KeyReleasedEvent(long window, int key, int codepoint) {
			this(window, key, codepoint, new AtomicBoolean());
		}
		
		@Override
		public void consume() {
			consumed.set(true);
		}
	}
	
	/**
	 * KeyEvent where a key was just pressed.
	 * <p>
	 * This differs from {@link KeyPressedEvent} in that it will only be fired once per key press.
	 * @param window The window ID the key was triggered from.
	 * @param key The key that triggered the event
	 * @param codepoint The character produced by this event.
	 */
	public record KeyTypedEvent(long window, int key, int codepoint) implements KeyEvent {
		
	}

}
