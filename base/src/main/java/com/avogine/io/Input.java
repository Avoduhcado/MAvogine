package com.avogine.io;

import java.nio.DoubleBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import com.avogine.io.event.Event;
import com.avogine.io.event.KeyboardEvent;
import com.avogine.io.event.MouseClickEvent;
import com.avogine.io.event.MouseMotionEvent;
import com.avogine.io.event.MouseScrollEvent;
import com.avogine.io.listener.InputListener;
import com.avogine.io.listener.KeyboardListener;
import com.avogine.io.listener.MouseClickListener;
import com.avogine.io.listener.MouseMotionListener;
import com.avogine.io.listener.MouseScrollListener;

/**
 * {@link Input} controls processing all user inputs on a per {@link Window} basis.
 * <p>
 * After a {@code Window} and GLFW context is properly created and initialized, an {@code Input} should
 * be initialized for that {@code Window} in order to be able to handle any keyboard or mouse input, 
 * converting them into proper {@link Event}s and firing them off to any registered {@link InputListener}s.
 */
public class Input {

	private final Map<Long, Set<InputListener>> listeners;
	private final boolean[] keys;
	
	private float lastMouseX;
	private float lastMouseY;
	
	/**
	 * Create a new {@link Input} and populate a {@code boolean} array to map all button presses.
	 */
	public Input() {
		listeners = new HashMap<>();
		
		keys = new boolean[GLFW.GLFW_KEY_LAST];
		Arrays.fill(keys, false);
	}
	
	/**
	 * @param register
	 */
	public void init(Window register) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			DoubleBuffer xPos = stack.mallocDouble(1);
			DoubleBuffer yPos = stack.mallocDouble(1);
			
			GLFW.glfwGetCursorPos(register.getId(), xPos, yPos);
			lastMouseX = (float) xPos.get();
			lastMouseY = (float) yPos.get();
		}
		
		GLFW.glfwSetKeyCallback(register.getId(), (window, key, scancode, action, mods) -> {
			fireKeyboardEvent(window, new KeyboardEvent(action, key, window));
			// XXX Hmm, this seems like a terrible way to handle this
			if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
				GLFW.glfwSetWindowShouldClose(window, true);
			}
			
			if (key == GLFW.GLFW_KEY_F3 && action == GLFW.GLFW_RELEASE) {
				Window.debugMode = !Window.debugMode;
			}
		});
		
		GLFW.glfwSetMouseButtonCallback(register.getId(), (window, button, action, mods) -> 
			fireMouseClickEvent(window, new MouseClickEvent(window, button, lastMouseX, lastMouseY, action)));
		
		GLFW.glfwSetCursorPosCallback(register.getId(), (window, xPos, yPos) -> {
			fireMouseMotionEvent(window, new MouseMotionEvent(window, xPos, yPos, xPos - lastMouseX, lastMouseY - yPos));
			lastMouseX = (float) xPos;
			lastMouseY = (float) yPos;
		});
		
		GLFW.glfwSetScrollCallback(register.getId(), (window, xOffset, yOffset) ->
			fireMouseScrollEvent(window, new MouseScrollEvent(window, xOffset, yOffset)));
	}
	
	/**
	 * @param window
	 */
	public void update(Window window) {
		// GLFW_REPEAT has god awful lag, so we're gonna roll our own keyDown events
		// Perhaps in the future we'll only update the array of specified key bindings rather than all accessible keys.
		for (int i = GLFW.GLFW_KEY_SPACE; i < keys.length; i++) {
			if (GLFW.glfwGetKey(window.getId(), i) == GLFW.GLFW_PRESS) {
				fireKeyboardEvent(window.getId(), new KeyboardEvent(GLFW.GLFW_PRESS, i, window.getId()));
			}
		}
		
		// TODO Add a repeatable mouse held loop? Doesn't seem necessary?
		for (int i = GLFW.GLFW_MOUSE_BUTTON_1; i < GLFW.GLFW_MOUSE_BUTTON_LAST; i++) {
			if (GLFW.glfwGetMouseButton(window.getId(), i) == GLFW.GLFW_PRESS) {
				// Should this be a different type of event? MouseHeldEvent?
				fireMouseClickEvent(window.getId(), new MouseClickEvent(window.getId(), i, lastMouseX, lastMouseY, GLFW.GLFW_REPEAT));
			}
		}
	}
	
	/**
	 * @param id
	 * @param l
	 * @return
	 */
	public InputListener add(long id, InputListener l) {
		listeners.computeIfAbsent(id, v -> new HashSet<>()).add(l);
		return l;
	}
	
	/**
	 * @param window
	 * @param l
	 * @return
	 */
	public InputListener add(Window window, InputListener l) {
		return add(window.getId(), l);
	}
	
	/**
	 * @param id
	 * @param listener
	 * @return
	 */
	public InputListener removeListener(long id, InputListener listener) {
		if (listeners.containsKey(id)) {
			listeners.get(id).remove(listener);
		}
		return listener;
	}

	/**
	 * @param window
	 * @param l
	 * @return
	 */
	public InputListener removeListener(Window window, InputListener l) {
		return removeListener(window.getId(), l);
	}
	
	/**
	 * @param <T>
	 * @param id
	 * @param clazz
	 * @return
	 */
	public <T extends InputListener> Stream<T> getListenersOfType(long id, Class<T> clazz) {
		return listeners.getOrDefault(id, Set.of()).stream()
				.filter(clazz::isInstance)
				.map(clazz::cast);
	}
	
	private void fireKeyboardEvent(long id, KeyboardEvent event) {
		getListenersOfType(id, KeyboardListener.class)
			.forEach(kl -> kl.keyPressed(event));
	}
	
	private void fireMouseClickEvent(long id, MouseClickEvent event) {
		getListenersOfType(id, MouseClickListener.class)
			.forEach(mcl -> mcl.mouseClicked(event));
	}
	
	private void fireMouseMotionEvent(long id, MouseMotionEvent event) {
		getListenersOfType(id, MouseMotionListener.class)
			.forEach(mml -> mml.mouseMoved(event));
	}
	
	private void fireMouseScrollEvent(long id, MouseScrollEvent event) {
		getListenersOfType(id, MouseScrollListener.class)
			.forEach(msl -> msl.mouseScrolled(event));
	}
	
}
