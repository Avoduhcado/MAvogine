package com.avogine.io;

import java.util.*;

import org.lwjgl.glfw.GLFW;

import com.avogine.io.config.InputConfig;
import com.avogine.io.event.*;
import com.avogine.io.event.KeyEvent.*;
import com.avogine.io.event.MouseEvent.*;
import com.avogine.io.listener.*;

/**
 * {@link Input} controls processing all user inputs on a per {@link Window} basis.
 * <p>
 * After a {@code Window} and GLFW context is properly created and initialized, an {@code Input} should
 * be initialized for that {@code Window} in order to be able to handle any keyboard or mouse input, 
 * converting them into proper {@link AvoEvent}s and firing them off to any registered {@link InputListener}s.
 */
public class Input {
	
	private final Window registeredWindow;
	
	private final Keyboard keyboard;
	private final Mouse mouse;
	
	private final Set<InputListener> listeners;
	
	/**
	 * Create a new {@link Input} and register it to a given {@link Window} to process {@link AvoEvent}s for.
	 * <p>
	 * Populates an initial {@code boolean} array to map all button presses along with click delay times for all mouse buttons.
	 * @param window the {@code Window} to process {@code Event}s for.
	 */
	public Input(Window window) {
		registeredWindow = window;
		
		keyboard = new Keyboard();
		mouse = new Mouse();
		
		listeners = new HashSet<>();
	}
	
	/**
	 * 
	 * @param config
	 */
	public void init(InputConfig config) {
		mouse.init(config, getWindowHandle());
		
		configureKeyCallback();
		configureCharCallback();
		
		configureMouseButtonCallback();
		configureCursorPosCallback();
		configureScrollCallback();
	}
	
	private void configureKeyCallback() {
		GLFW.glfwSetKeyCallback(getWindowHandle(), (window, key, scancode, action, mods) -> {
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
	
	private void configureCharCallback() {
		GLFW.glfwSetCharCallback(getWindowHandle(), (window, codepoint) -> fireCharEvent(new CharEvent(registeredWindow, codepoint)));
	}
	
	private void configureMouseButtonCallback() {
		GLFW.glfwSetMouseButtonCallback(getWindowHandle(), (window, button, action, mods) -> {
			mouse.setButton(button, action == GLFW.GLFW_PRESS);
			int clickCount = mouse.getButtonClickCount(button);
			
			switch (action) {
				case GLFW.GLFW_PRESS -> fireMouseButtonEvent(new MousePressedEvent(registeredWindow, button, clickCount, mouse.getPosition().x(), mouse.getPosition().y()));
				case GLFW.GLFW_RELEASE -> {
					fireMouseButtonEvent(new MouseReleasedEvent(registeredWindow, button, clickCount, mouse.getPosition().x(), mouse.getPosition().y()));
					
					if (clickCount > 0) {
						fireMouseButtonEvent(new MouseClickedEvent(registeredWindow, button, clickCount, mouse.getPosition().x(), mouse.getPosition().y()));
					}
				}
				default -> throw new IllegalArgumentException("Cannot process mouse button callback event for action: " + action);
			}
		});
	}
	
	private void configureCursorPosCallback() {
		GLFW.glfwSetCursorPosCallback(getWindowHandle(), (window, xPos, yPos) -> {
			mouse.setPosition(xPos, yPos);
			
			for (int i = GLFW.GLFW_MOUSE_BUTTON_1; i < GLFW.GLFW_MOUSE_BUTTON_LAST; i++) {
				if (GLFW.glfwGetMouseButton(getWindowHandle(), i) == GLFW.GLFW_PRESS) {
					fireMouseMotionEvent(new MouseDraggedEvent(registeredWindow, i, (float) xPos, (float) yPos));
				}
			}
			fireMouseMotionEvent(new MouseMovedEvent(registeredWindow, (float) xPos, (float) yPos));
		});
	}
	
	private void configureScrollCallback() {
		GLFW.glfwSetScrollCallback(getWindowHandle(), (window, xOffset, yOffset) -> {
			mouse.setScroll(xOffset, yOffset);
			
			fireMouseScrollEvent(new MouseWheelEvent(registeredWindow, mouse.getPosition().x(), mouse.getPosition().y(), xOffset, yOffset));
		});
	}
	
	private long getWindowHandle() {
		return registeredWindow.getId();
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
	 * @return the removed {@link InputListener}
	 */
	public InputListener removeInputListener(InputListener listener) {
		listeners.remove(listener);
		return listener;
	}
	
	/**
	 * @return the keyboard
	 */
	public Keyboard getKeyboard() {
		return keyboard;
	}
	
	/**
	 * @return the mouse
	 */
	public Mouse getMouse() {
		return mouse;
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
	
	private void fireMouseButtonEvent(MouseButtonEvent event) {
		for (var listener : listeners) {
			if (listener instanceof MouseButtonListener mbl && !event.isConsumed()) {
				switch (event) {
					case MousePressedEvent e -> mbl.mousePressed(e);
					case MouseReleasedEvent e -> mbl.mouseReleased(e);
					case MouseClickedEvent e -> mbl.mouseClicked(e);
					case MouseDraggedEvent e -> {
						// Drag events should be handled by MouseMotionListeners
					}
				}
			}
		}
	}
	
	private void fireMouseMotionEvent(MouseMotionEvent event) {
		for (var listener : listeners) {
			if (listener instanceof MouseMotionListener mml && !event.isConsumed()) {
				switch (event) {
					case MouseDraggedEvent e -> mml.mouseDragged(e);
					case MouseMovedEvent e -> mml.mouseMoved(e);
				}
			}
		}
	}
	
	private void fireMouseScrollEvent(MouseWheelEvent event) {
		for (var listener : listeners) {
			if (listener instanceof MouseWheelListener mwl && !event.isConsumed()) {
				mwl.mouseWheelMoved(event);
			}
		}
	}
	
}
