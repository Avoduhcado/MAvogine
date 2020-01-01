package com.avogine.core.system;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.lwjgl.glfw.GLFW;

import com.avogine.core.system.event.KeyboardEvent;
import com.avogine.core.system.event.MouseClickEvent;
import com.avogine.core.system.event.MouseMotionEvent;
import com.avogine.core.system.event.MouseScrollEvent;
import com.avogine.core.system.listener.InputListener;
import com.avogine.core.system.listener.KeyboardListener;
import com.avogine.core.system.listener.MouseClickListener;
import com.avogine.core.system.listener.MouseMotionListener;
import com.avogine.core.system.listener.MouseScrollListener;

public class Input {

	private static final Map<Long, Set<InputListener>> listeners;
	private static boolean[] keys;
	
	static {
		listeners = new HashMap<>();
		
		keys = new boolean[GLFW.GLFW_KEY_LAST];
		Arrays.fill(keys, false);
	}
	
	public static void registerWindow(Theater theater) {
		GLFW.glfwSetKeyCallback(theater.getId(), (window, key, scancode, action, mods) -> {
			fireKeyboardEvent(window, new KeyboardEvent(action, key, window));
			// XXX Hmm, this seems like a terrible way to handle this
			if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
				GLFW.glfwSetWindowShouldClose(window, true);
			}
			
			if (key == GLFW.GLFW_KEY_F3 && action == GLFW.GLFW_RELEASE) {
				Theater.debugMode = !Theater.debugMode;
			}
		});
		
		GLFW.glfwSetMouseButtonCallback(theater.getId(), (window, button, action, mods) -> {
			fireMouseClickEvent(window, new MouseClickEvent(window, button, action));
		});
		
		GLFW.glfwSetCursorPosCallback(theater.getId(), (window, xPos, yPos) -> {
			fireMouseMotionEvent(window, new MouseMotionEvent(window, xPos, yPos));
		});
		
		GLFW.glfwSetScrollCallback(theater.getId(), (window, xOffset, yOffset) -> {
			fireMouseScrollEvent(window, new MouseScrollEvent(window, xOffset, yOffset));
		});
	}
	
	public static void update(Theater theater) {
		// GLFW#GLFW_REPEAT has god awful lag, so we're gonna roll our own keyDown events
		// Perhaps in the future we'll only update the array of specified key bindings rather than all accessible keys.
		for (int i = GLFW.GLFW_KEY_SPACE; i < keys.length; i++) {
			if (GLFW.glfwGetKey(theater.getId(), i) == GLFW.GLFW_PRESS) {
				fireKeyboardEvent(theater.getId(), new KeyboardEvent(GLFW.GLFW_PRESS, i, theater.getId()));
			}
		}
		
		// TODO Add a repeatable mouse held loop? Doesn't seem necessary?
	}
	
	public static InputListener add(long id, InputListener l) {
		listeners.computeIfAbsent(id, v -> new HashSet<>()).add(l);
		return l;
	}
	
	public static InputListener removeListener(long id, InputListener listener) {
		if (listeners.containsKey(id)) {
			listeners.get(id).remove(listener);
		}
		return listener;
	}
	
	public static <T extends InputListener> Stream<T> getListenersOfType(long id, Class<T> clazz) {
		return listeners.get(id).stream()
				.filter(clazz::isInstance)
				.map(clazz::cast);
	}
	
	private static void fireKeyboardEvent(long id, KeyboardEvent event) {
		getListenersOfType(id, KeyboardListener.class)
			.forEach(kl -> kl.keyPressed(event));
	}
	
	private static void fireMouseClickEvent(long id, MouseClickEvent event) {
		getListenersOfType(id, MouseClickListener.class)
			.forEach(mcl -> mcl.mouseClicked(event));
	}
	
	private static void fireMouseMotionEvent(long id, MouseMotionEvent event) {
		getListenersOfType(id, MouseMotionListener.class)
			.forEach(mml -> mml.mouseMoved(event));
	}
	
	private static void fireMouseScrollEvent(long id, MouseScrollEvent event) {
		getListenersOfType(id, MouseScrollListener.class)
			.forEach(msl -> msl.mouseScrolled(event));
	}
	
}
