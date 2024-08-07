package com.avogine.io;

import java.nio.DoubleBuffer;
import java.util.*;
import java.util.stream.Stream;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import com.avogine.io.config.InputConfig;
import com.avogine.io.event.*;
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
	
	/**
	 * 
	 */
	public void update() {
		// GLFW_REPEAT has god awful lag, so we're going to roll our own keyDown events
		// Perhaps in the future we'll only update the array of specified key bindings rather than all accessible keys.
		for (int i = GLFW.GLFW_KEY_SPACE; i < keys.length; i++) {
			if (GLFW.glfwGetKey(windowID, i) == GLFW.GLFW_PRESS) {
				if (!keys[i]) {
					fireKeyboardEvent(new KeyEvent(KeyEvent.KEY_TYPED, i, ' ', windowID));
				}
				keys[i] = true;
			} else {
				keys[i] = false;
			}
		}
	}
	
	private void configureKeyCallback() {
		GLFW.glfwSetKeyCallback(windowID, (window, key, scancode, action, mods) -> {
			if (action == GLFW.GLFW_REPEAT) {
				return;
			}
			fireKeyboardEvent(new KeyEvent(getKeyEventId(action), key, ' ', window));
			// XXX Hmm, this seems like a terrible way to handle this
			if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
				GLFW.glfwSetWindowShouldClose(window, true);
			}
			
			if (key == GLFW.GLFW_KEY_F3 && action == GLFW.GLFW_RELEASE) {
				registeredWindow.setDebugMode(!registeredWindow.isDebugMode());
			}
		});
	}
	
	private void configureCharCallback() {
		GLFW.glfwSetCharCallback(windowID, (window, codepoint) -> fireKeyboardEvent(new KeyEvent(KeyEvent.KEY_TYPED, -1, (char)codepoint, window)));
	}
	
	private void configureMouseButtonCallback() {
		GLFW.glfwSetMouseButtonCallback(windowID, (window, button, action, mods) -> {
			int clickCount = 1;
			if (action == GLFW.GLFW_PRESS) {
				lastMouseClick.set(lastMouseX, lastMouseY);
			}
			
			fireMouseButtonEvent(new MouseEvent(getMouseEventId(action), button, clickCount, lastMouseX, lastMouseY, window));
			if (action == GLFW.GLFW_RELEASE && lastMouseClick.distance(lastMouseX, lastMouseY) <= clickDriftTolerance) {
				double time = GLFW.glfwGetTime();
				if (time - clickTimes.replace(button, time) <= doubleClickDelayTolerance) {
					clickCount += 1;
				}
				lastMouseClick.set(lastMouseX, lastMouseY);
				fireMouseButtonEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, button, clickCount, lastMouseX, lastMouseY, window));
			}
		});
	}
	
	private void configureCursorPosCallback() {
		GLFW.glfwSetCursorPosCallback(windowID, (window, xPos, yPos) -> {
			boolean dragged = false;
			for (int i = GLFW.GLFW_MOUSE_BUTTON_1; i < GLFW.GLFW_MOUSE_BUTTON_LAST; i++) {
				if (GLFW.glfwGetMouseButton(windowID, i) == GLFW.GLFW_PRESS) {
					dragged = true;
					fireMouseMotionEvent(new MouseEvent(MouseEvent.MOUSE_DRAGGED, i, 1, (float) xPos, (float) yPos, window));
				}
			}
			if (!dragged) {
				fireMouseMotionEvent(new MouseEvent(MouseEvent.MOUSE_MOVED, 0, 0, (float) xPos, (float) yPos, window));
			}
			lastMouseX = (float) xPos;
			lastMouseY = (float) yPos;
		});
	}
	
	private void configureScrollCallback() {
		GLFW.glfwSetScrollCallback(windowID, (window, xOffset, yOffset) -> fireMouseScrollEvent(new MouseWheelEvent(xOffset, yOffset, window)));
	}
	
	/**
	 * Register an {@link InputListener} to this {@link Input} and store
	 * a reference to it for potential de-registering later.
	 * @param l The {@code InputListener} to add.
	 * @return 
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
	 * @param <T>
	 * @param clazz
	 * @return a {@code Stream} of {@link InputListener}s that are all the same type as {@code clazz}
	 */
	private <T extends InputListener> Stream<T> getListenersOfType(Class<T> clazz) {
		return listeners.stream()
				.sorted()
				.filter(clazz::isInstance)
				.map(clazz::cast);
	}
	
	private void fireKeyboardEvent(KeyEvent event) {
		getListenersOfType(KeyListener.class)
		.takeWhile(l -> !event.isConsumed())
		.forEach(kl -> {
			switch (event.id()) {
				case KeyEvent.KEY_PRESSED -> kl.keyPressed(event);
				case KeyEvent.KEY_RELEASED -> kl.keyReleased(event);
				case KeyEvent.KEY_TYPED -> kl.keyTyped(event);
			}
		});
	}
	
	private void fireMouseButtonEvent(MouseEvent event) {
		getListenersOfType(MouseButtonListener.class)
		.takeWhile(l -> !event.isConsumed())
		.forEach(mcl -> {
			switch (event.id()) {
				case MouseEvent.MOUSE_PRESSED -> mcl.mousePressed(event);
				case MouseEvent.MOUSE_RELEASED -> mcl.mouseReleased(event);
				case MouseEvent.MOUSE_CLICKED -> mcl.mouseClicked(event);
			}
		});
	}
	
	private void fireMouseMotionEvent(MouseEvent event) {
		getListenersOfType(MouseMotionListener.class)
		.takeWhile(l -> !event.isConsumed())
		.forEach(mml -> {
			switch (event.id()) {
				case MouseEvent.MOUSE_MOVED -> mml.mouseMoved(event);
				case MouseEvent.MOUSE_DRAGGED -> mml.mouseDragged(event);
			}
		});
	}
	
	private void fireMouseScrollEvent(MouseWheelEvent event) {
		getListenersOfType(MouseWheelListener.class)
		.takeWhile(l -> !event.isConsumed())
		.forEach(msl -> msl.mouseWheelMoved(event));
	}
	
	private int getKeyEventId(int action) {
		return switch (action) {
			case GLFW.GLFW_PRESS -> KeyEvent.KEY_PRESSED;
			case GLFW.GLFW_RELEASE -> KeyEvent.KEY_RELEASED;
			default -> -1;
		};
	}
	
	private int getMouseEventId(int action) {
		return switch (action) {
			case GLFW.GLFW_PRESS -> MouseEvent.MOUSE_PRESSED;
			case GLFW.GLFW_RELEASE -> MouseEvent.MOUSE_RELEASED;
			default -> -1;
		};
	}
	
}
