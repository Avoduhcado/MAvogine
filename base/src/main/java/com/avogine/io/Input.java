package com.avogine.io;

import java.nio.DoubleBuffer;
import java.util.*;
import java.util.stream.Stream;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import com.avogine.io.event.*;
import com.avogine.io.listener.*;

/**
 * {@link Input} controls processing all user inputs on a per {@link Window} basis.
 * <p>
 * After a {@code Window} and GLFW context is properly created and initialized, an {@code Input} should
 * be initialized for that {@code Window} in order to be able to handle any keyboard or mouse input, 
 * converting them into proper {@link Event}s and firing them off to any registered {@link InputListener}s.
 */
public class Input {

	private Window registeredWindow;
	private long windowID;
		
	private final Set<InputListener> listeners;
	// True if the key at index is currently pressed
	private final boolean[] keys;
	
	private float lastMouseX;
	private float lastMouseY;
	
	/**
	 * Create a new {@link Input} and populate a {@code boolean} array to map all button presses.
	 */
	public Input() {
		listeners = new HashSet<>();
		
		keys = new boolean[GLFW.GLFW_KEY_LAST];
		Arrays.fill(keys, false);
	}
	
	/**
	 * Register this {@code Input} to process {@link Event}s for a given {@link Window}.
	 * @param registeredWindow the {@code Window} to process {@code Event}s for.
	 */
	public void init(Window registeredWindow) {
		this.registeredWindow = registeredWindow;
		this.windowID = registeredWindow.getId();
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			DoubleBuffer xPos = stack.mallocDouble(1);
			DoubleBuffer yPos = stack.mallocDouble(1);
			
			GLFW.glfwGetCursorPos(windowID, xPos, yPos);
			lastMouseX = (float) xPos.get();
			lastMouseY = (float) yPos.get();
		}
		
		GLFW.glfwSetKeyCallback(windowID, (window, key, scancode, action, mods) -> {
			if (action == GLFW.GLFW_REPEAT) {
				return;
			}
			fireKeyboardEvent(new KeyboardEvent(action, key, window));
			// XXX Hmm, this seems like a terrible way to handle this
			if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
				GLFW.glfwSetWindowShouldClose(window, true);
			}
			
			if (key == GLFW.GLFW_KEY_F3 && action == GLFW.GLFW_RELEASE) {
				Window.debugMode = !Window.debugMode;
			}
		});
		
		GLFW.glfwSetMouseButtonCallback(windowID, (window, button, action, mods) -> 
			fireMouseClickEvent(new MouseClickEvent(action, button, lastMouseX, lastMouseY, window)));
		
		GLFW.glfwSetCursorPosCallback(windowID, (window, xPos, yPos) -> {
			fireMouseMotionEvent(new MouseMotionEvent(xPos, yPos, xPos - lastMouseX, lastMouseY - yPos, window));
			lastMouseX = (float) xPos;
			lastMouseY = (float) yPos;
		});
		
		GLFW.glfwSetScrollCallback(windowID, (window, xOffset, yOffset) ->
			fireMouseScrollEvent(new MouseScrollEvent(xOffset, yOffset, window)));
	}
	
	/**
	 */
	public void update() {
		// GLFW_REPEAT has god awful lag, so we're gonna roll our own keyDown events
		// Perhaps in the future we'll only update the array of specified key bindings rather than all accessible keys.
		for (int i = GLFW.GLFW_KEY_SPACE; i < keys.length; i++) {
			if (GLFW.glfwGetKey(windowID, i) == GLFW.GLFW_PRESS) {
				if (!keys[i]) {
					fireKeyboardEvent(new KeyboardEvent(KeyboardEvent.KEY_TYPED, i, windowID));
				} else {
					fireKeyboardEvent(new KeyboardEvent(GLFW.GLFW_PRESS, i, windowID));
				}
				keys[i] = true;
			} else {
				keys[i] = false;
			}
		}
		
		// TODO Add a repeatable mouse held loop? Doesn't seem necessary?
		for (int i = GLFW.GLFW_MOUSE_BUTTON_1; i < GLFW.GLFW_MOUSE_BUTTON_LAST; i++) {
			if (GLFW.glfwGetMouseButton(windowID, i) == GLFW.GLFW_PRESS) {
				// Should this be a different type of event? MouseHeldEvent?
				fireMouseClickEvent(new MouseClickEvent(GLFW.GLFW_REPEAT, i, lastMouseX, lastMouseY, windowID));
			}
		}
	}
	
	/**
	 * @param l
	 * @return the attached {@link InputListener}
	 */
	public InputListener add(InputListener l) {
		listeners.add(l);
		return l;
	}
	
	/**
	 * @param listener
	 * @return the removed {@link InputListener}
	 */
	public InputListener removeListener(InputListener listener) {
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
				.filter(clazz::isInstance)
				.map(clazz::cast);
	}
	
	private void fireKeyboardEvent(KeyboardEvent event) {
		getListenersOfType(KeyboardListener.class)
			.forEach(kl -> {
				switch (event.type()) {
				case GLFW.GLFW_PRESS -> kl.keyPressed(event);
				case GLFW.GLFW_RELEASE -> kl.keyReleased(event);
				case KeyboardEvent.KEY_TYPED -> kl.keyTyped(event);
				}
			});
	}
	
	private void fireMouseClickEvent(MouseClickEvent event) {
		getListenersOfType(MouseClickListener.class)
			.forEach(mcl -> mcl.mouseClicked(event));
	}
	
	private void fireMouseMotionEvent(MouseMotionEvent event) {
		getListenersOfType(MouseMotionListener.class)
			.forEach(mml -> mml.mouseMoved(event));
	}
	
	private void fireMouseScrollEvent(MouseScrollEvent event) {
		getListenersOfType(MouseScrollListener.class)
			.forEach(msl -> msl.mouseScrolled(event));
	}
	
}
