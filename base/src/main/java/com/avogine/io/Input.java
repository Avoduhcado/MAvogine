package com.avogine.io;

import com.avogine.io.config.InputConfig;
import com.avogine.io.event.AvoEvent;
import com.avogine.io.listener.*;

/**
 * {@link Input} controls processing all user inputs on a per {@link Window} basis.
 * <p>
 * After a {@code Window} and GLFW context is properly created and initialized, an {@code Input} should
 * be initialized for that {@code Window} in order to be able to handle any keyboard or mouse input, 
 * converting them into proper {@link AvoEvent}s and firing them off to any registered {@link InputListener}s.
 */
public class Input {
	
	private final KeyboardInput keyboard;
	private final MouseInput mouse;
	
	/**
	 * Create a new {@link Input} and register it to a given {@link Window} to process {@link AvoEvent}s for.
	 * <p>
	 * Populates an initial {@code boolean} array to map all button presses along with click delay times for all mouse buttons.
	 */
	public Input() {
		keyboard = new KeyboardInput();
		mouse = new MouseInput();
	}
	
	/**
	 * 
	 * @param config
	 * @param window 
	 */
	public void init(InputConfig config, Window window) {
		keyboard.init(window);
		mouse.init(config, window);
	}
	
	/**
	 * Register an {@link InputListener} to this {@link KeyboardInput} and/or {@link MouseInput} and store
	 * a reference to it for potential de-registering later.
	 * @param l The {@code InputListener} to add.
	 * @return the {@code InputListener}.
	 */
	public InputListener addInputListener(InputListener l) {
		if (l instanceof KeyListener || l instanceof CharListener) {
			keyboard.addInputListener(l);
		}
		if (l instanceof MouseButtonListener || l instanceof MouseMotionListener || l instanceof MouseWheelListener) {
			mouse.addInputListener(l);
		}
		return l;
	}
	
	/**
	 * @param listener
	 * @return true if either the {@link MouseInput} or {@link KeyboardInput} contained the listener.
	 */
	public boolean removeInputListener(InputListener listener) {
		boolean keyboardRemoved = keyboard.removeInputListener(listener);
		boolean mouseRemoved = mouse.removeInputListener(listener);
		return keyboardRemoved || mouseRemoved;
	}
	
	/**
	 * @return the keyboard
	 */
	public Keyboard getKeyboard() {
		return keyboard.getKeyboard();
	}
	
	/**
	 * @return the mouse
	 */
	public Mouse getMouse() {
		return mouse.getMouse();
	}
}
