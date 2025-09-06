package com.avogine.io.event;

import java.util.concurrent.atomic.AtomicBoolean;

import com.avogine.io.Window;
import com.avogine.io.listener.KeyListener;

/**
 * Base interface for {@link InputEvent}s fired by keyboard input.
 */
public sealed interface KeyEvent extends InputEvent, ConsumableEvent {
	/**
	 * @return the keyboard key that was pressed or released.
	 */
	public int key();
	
	/**
	 * @return the platform-specific scancode of the key.
	 */
	public int scancode();
	
	/**
	 * @return bitfield describing which modifiers keys were held down.
	 */
	public int mods();

	/**
	 * KeyEvent where a key is pressed.
	 * <p>
	 * This will continue to fire new events while the key is pressed.
	 * @param window The window the key was triggered from.
	 * @param key the keyboard key that was pressed.
	 * @param scancode the platform-specific scancode of the key.
	 * @param mods Bitfield describing which modifiers keys were held down.
	 * @param consumed true only if this event has been handled and consumed by a {@link KeyListener}.
	 */
	public record KeyPressedEvent(Window window, int key, int scancode, int mods, AtomicBoolean consumed) implements KeyEvent {
		/**
		 * @param window The window the key was triggered from.
		 * @param key The key that triggered the event
		 * @param scancode The platform-specific scancode of the key.
		 * @param mods Bitfield describing which modifiers keys were held down.
		 */
		public KeyPressedEvent(Window window, int key, int scancode, int mods) {
			this(window, key, scancode, mods, new AtomicBoolean());
		}
		
		@Override
		public void consume() {
			consumed.set(true);
		}
	}
	
	/**
	 * KeyEvent where a key was released.
	 * @param window The window the key was triggered from.
	 * @param key The keyboard key that was released.
	 * @param scancode The platform-specific scancode of the key.
	 * @param mods Bitfield describing which modifiers keys were held down.
	 * @param consumed true only if this event has been handled and consumed by a {@link KeyListener}.
	 */
	public record KeyReleasedEvent(Window window, int key, int scancode, int mods, AtomicBoolean consumed) implements KeyEvent {
		/**
		 * @param window The window the key was triggered from.
		 * @param key The key that triggered the event
		 * @param scancode The character produced by this event.
		 * @param mods Bitfield describing which modifiers keys were held down.
		 */
		public KeyReleasedEvent(Window window, int key, int scancode, int mods) {
			this(window, key, scancode, mods, new AtomicBoolean());
		}
		
		@Override
		public void consume() {
			consumed.set(true);
		}
	}
	
}
