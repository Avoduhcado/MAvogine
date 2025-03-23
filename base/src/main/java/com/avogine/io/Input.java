package com.avogine.io;

import java.nio.DoubleBuffer;
import java.util.*;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

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
	
	private Window registeredWindow;
	private long windowID;
	
	private final Set<InputListener> listeners;
	// True if the key at index is currently pressed
	private final boolean[] keys;
	
	private float lastMouseX;
	private float lastMouseY;
	private final Vector2f lastMouseClick;
	
	private final Map<Integer, Double> clickTimes;
	private float clickDriftTolerance;
	private float doubleClickDelayTolerance;
	
	/**
	 * Create a new {@link Input} and register it to a given {@link Window} to process {@link AvoEvent}s for.
	 * <p>
	 * Populates an initial {@code boolean} array to map all button presses along with click delay times for all mouse buttons.
	 * @param window the {@code Window} to process {@code Event}s for.
	 */
	public Input(Window window) {
		registeredWindow = window;
		
		listeners = new HashSet<>();
		
		keys = new boolean[GLFW.GLFW_KEY_LAST];
		Arrays.fill(keys, false);
		clickTimes = new HashMap<>();
		for (int i = GLFW.GLFW_MOUSE_BUTTON_1; i < GLFW.GLFW_MOUSE_BUTTON_LAST; i++) {
			clickTimes.put(i, 0.0);
		}
		lastMouseClick = new Vector2f();
	}
	
	/**
	 * 
	 * @param config
	 */
	public void init(InputConfig config) {
		clickDriftTolerance = config.clickDriftTolerance();
		doubleClickDelayTolerance = config.doubleClickDelayTolerance();

		windowID = registeredWindow.getId();
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			DoubleBuffer xPos = stack.mallocDouble(1);
			DoubleBuffer yPos = stack.mallocDouble(1);
			
			GLFW.glfwGetCursorPos(windowID, xPos, yPos);
			lastMouseX = (float) xPos.get();
			lastMouseY = (float) yPos.get();
		}
		
		configureKeyCallback();
		configureCharCallback();
		
		configureMouseButtonCallback();
		configureCursorPosCallback();
		configureScrollCallback();
	}
	
	private void configureKeyCallback() {
		GLFW.glfwSetKeyCallback(windowID, (window, key, scancode, action, mods) -> {
			switch (action) {
				case GLFW.GLFW_REPEAT -> {
					// No handling for GLFW_REPEAT
				}
				case GLFW.GLFW_PRESS ->  {
					if (key >= GLFW.GLFW_KEY_SPACE && key < keys.length) {
						keys[key] = true;
					}
					fireKeyboardEvent(new KeyPressedEvent(window, key, scancode, mods));
				}
				case GLFW.GLFW_RELEASE -> {
					if (key >= GLFW.GLFW_KEY_SPACE && key < keys.length) {
						keys[key] = false;
					}
					fireKeyboardEvent(new KeyReleasedEvent(window, key, scancode, mods));
					if (key == GLFW.GLFW_KEY_F3) {
						registeredWindow.setDebugMode(!registeredWindow.isDebugMode());
					}
				}
				default -> throw new IllegalArgumentException("Cannot process key callback event for action: " + action);
			}
		});
	}
	
	private void configureCharCallback() {
		GLFW.glfwSetCharCallback(windowID, (window, codepoint) -> fireCharEvent(new CharEvent(window, codepoint)));
	}
	
	private void configureMouseButtonCallback() {
		GLFW.glfwSetMouseButtonCallback(windowID, (window, button, action, mods) -> {
			int clickCount = 1;
			switch (action) {
				case GLFW.GLFW_PRESS -> {
					lastMouseClick.set(lastMouseX, lastMouseY);
					fireMouseButtonEvent(new MousePressedEvent(window, button, clickCount, lastMouseX, lastMouseY));
				}
				case GLFW.GLFW_RELEASE -> {
					fireMouseButtonEvent(new MouseReleasedEvent(window, button, clickCount, lastMouseX, lastMouseY));
					if (lastMouseClick.distance(lastMouseX, lastMouseY) <= clickDriftTolerance) {
						double time = GLFW.glfwGetTime();
						if (time - clickTimes.replace(button, time) <= doubleClickDelayTolerance) {
							clickCount += 1;
						}
						lastMouseClick.set(lastMouseX, lastMouseY);
						fireMouseButtonEvent(new MouseClickedEvent(window, button, clickCount, lastMouseX, lastMouseY));
					}
				}
				default -> throw new IllegalArgumentException("Cannot process mouse button callback event for action: " + action);
			}
		});
	}
	
	private void configureCursorPosCallback() {
		GLFW.glfwSetCursorPosCallback(windowID, (window, xPos, yPos) -> {
			for (int i = GLFW.GLFW_MOUSE_BUTTON_1; i < GLFW.GLFW_MOUSE_BUTTON_LAST; i++) {
				if (GLFW.glfwGetMouseButton(windowID, i) == GLFW.GLFW_PRESS) {
					fireMouseMotionEvent(new MouseDraggedEvent(window, i, (float) xPos, (float) yPos));
				}
			}
			fireMouseMotionEvent(new MouseMovedEvent(window, (float) xPos, (float) yPos));
			lastMouseX = (float) xPos;
			lastMouseY = (float) yPos;
		});
	}
	
	private void configureScrollCallback() {
		GLFW.glfwSetScrollCallback(windowID, (window, xOffset, yOffset) -> fireMouseScrollEvent(new MouseWheelEvent(window, lastMouseX, lastMouseY, xOffset, yOffset)));
	}
	
	/**
	 * @return the keys
	 */
	public boolean[] getKeys() {
		return keys;
	}
	
	/**
	 * Return true if the given keyCode is currently pressed.
	 * </p>
	 * If {@code keyCode} is outside the inclusive range of {@link GLFW#GLFW_KEY_SPACE} and {@link GLFW#GLFW_KEY_LAST} this will
	 * throw an {@link IndexOutOfBoundsException}. 
	 * 
	 * @param keyCode
	 * @return true if the given keyCode is currently pressed.
	 * @throws IndexOutOfBoundsException If {@code keyCode} is outside the inclusive range of {@link GLFW#GLFW_KEY_SPACE} and {@link GLFW#GLFW_KEY_LAST}.
	 */
	public boolean isKeyDown(int keyCode) {
		Objects.checkIndex(keyCode, keys.length);
		return keys[keyCode];
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
