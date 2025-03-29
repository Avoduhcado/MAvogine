package com.avogine.io;

import java.util.*;

import org.lwjgl.glfw.GLFW;

import com.avogine.io.event.*;
import com.avogine.io.event.KeyEvent.*;
import com.avogine.io.listener.*;

/**
 *
 */
public class KeyboardInput {
	
	private final Keyboard keyboard;
	
	private final Set<InputListener> listeners;
	
	/**
	 * 
	 */
	public KeyboardInput() {
		keyboard = new Keyboard();
		
		listeners = new HashSet<>();
	}
	
	/**
	 * @param window
	 */
	public void init(Window window) {
		configureKeyCallback(window);
		configureCharCallback(window);
	}
	
	/**
	 * @return the keyboard
	 */
	public Keyboard getKeyboard() {
		return keyboard;
	}
	
	/**
	 * Register an {@link InputListener} to this {@link Input} and store
	 * a reference to it for potential de-registering later.
	 * @param l The {@code InputListener} to add.
	 * @return the {@code InputListener}.
	 */
	public InputListener addInputListener(InputListener l) {
		listeners.add(l);
		return l;
	}
	
	/**
	 * @param listener
	 * @return true if the set contained listener.
	 */
	public boolean removeInputListener(InputListener listener) {
		return listeners.remove(listener);
	}
	
	private void configureKeyCallback(Window registeredWindow) {
		GLFW.glfwSetKeyCallback(registeredWindow.getId(), (window, key, scancode, action, mods) -> {
			if (action == GLFW.GLFW_REPEAT) {
				return;
			}
			
			if (key >= GLFW.GLFW_KEY_SPACE && key <= GLFW.GLFW_KEY_LAST) {
				keyboard.setKey(key, action == GLFW.GLFW_PRESS);
			} else {
				keyboard.setScancode(scancode, action == GLFW.GLFW_PRESS);
			}
			keyboard.setMods(mods);
			
			switch (action) {
				case GLFW.GLFW_PRESS -> fireKeyboardEvent(new KeyPressedEvent(registeredWindow, key, scancode, mods));
				case GLFW.GLFW_RELEASE -> {
					fireKeyboardEvent(new KeyReleasedEvent(registeredWindow, key, scancode, mods));
					if (key == GLFW.GLFW_KEY_F3) {
						registeredWindow.setDebugMode(!registeredWindow.isDebugMode());
					}
				}
				default -> throw new IllegalArgumentException("Cannot process key callback event for action: " + action);
			}
		});
	}
	
	private void configureCharCallback(Window registeredWindow) {
		GLFW.glfwSetCharCallback(registeredWindow.getId(), (window, codepoint) -> fireCharEvent(new CharEvent(registeredWindow, codepoint)));
	}
	
	private void fireKeyboardEvent(KeyEvent event) {
		for (var listener : listeners) {
			if (listener instanceof KeyListener kl && !event.isConsumed()) {
				switch (event) {
					case KeyPressedEvent e -> kl.keyPressed(e);
					case KeyReleasedEvent e -> kl.keyReleased(e);
				}
			}
		}
	}
	
	private void fireCharEvent(CharEvent event) {
		for (var listener : listeners) {
			if (listener instanceof CharListener cl) {
				cl.charTyped(event);
			}
		}
	}
}
