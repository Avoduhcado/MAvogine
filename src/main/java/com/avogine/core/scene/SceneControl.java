package com.avogine.core.scene;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.lwjgl.glfw.GLFW;

import com.avogine.core.system.Theater;
import com.avogine.core.system.event.KeyboardEvent;
import com.avogine.core.system.event.MouseClickEvent;
import com.avogine.core.system.event.MouseMotionEvent;
import com.avogine.core.system.event.MouseScrollEvent;
import com.avogine.core.system.listener.InputListener;
import com.avogine.core.system.listener.KeyboardListener;
import com.avogine.core.system.listener.MouseClickListener;
import com.avogine.core.system.listener.MouseMotionListener;
import com.avogine.core.system.listener.MouseScrollListener;

/**
 * TODO Maybe make this unique per {@link Theater}.
 * @author Dominus
 *
 */
public class SceneControl {

	private static final Set<InputListener> listeners;
	private static boolean[] keys;
	
	static {
		listeners = new HashSet<>();
		
		keys = new boolean[GLFW.GLFW_KEY_LAST];
		Arrays.fill(keys, false);
	}
	
	public static void init(Theater theater) {
		GLFW.glfwSetKeyCallback(theater.getId(), (window, key, scancode, action, mods) -> {
			fireKeyboardEvent(new KeyboardEvent(action, key, window));
		});
		
		GLFW.glfwSetMouseButtonCallback(theater.getId(), (window, button, action, mods) -> {
			fireMouseClickEvent(new MouseClickEvent(window, button, action));
		});
		
		GLFW.glfwSetCursorPosCallback(theater.getId(), (window, xPos, yPos) -> {
			fireMouseMotionEvent(new MouseMotionEvent(window, xPos, yPos));
		});
		
		GLFW.glfwSetScrollCallback(theater.getId(), (window, xOffset, yOffset) -> {
			fireMouseScrollEvent(new MouseScrollEvent(window, xOffset, yOffset));
		});
	}
	
	public static void update(Theater theater) {
		// GLFW#GLFW_REPEAT has god awful lag, so we're gonna roll our own keyDown events
		// Perhaps in the future we'll only update the array of specified key bindings rather than all accessible keys.
		for (int i = GLFW.GLFW_KEY_SPACE; i < keys.length; i++) {
			if (GLFW.glfwGetKey(theater.getId(), i) == GLFW.GLFW_PRESS) {
				fireKeyboardEvent(new KeyboardEvent(GLFW.GLFW_PRESS, i, theater.getId()));
			}
		}
		
		// TODO Add a repeatable mouse held loop? Doesn't seem necessary?
	}
	
	public static InputListener add(InputListener l) {
		listeners.add(l);
		return l;
	}
	
	public static InputListener removeListener(InputListener listener) {
		listeners.remove(listener);
		return listener;
	}
	
	public static <T extends InputListener> Stream<T> getListenersOfType(Class<T> clazz) {
		return listeners.stream()
				.filter(clazz::isInstance)
				.map(clazz::cast);
	}
	
	private static void fireKeyboardEvent(KeyboardEvent event) {
		getListenersOfType(KeyboardListener.class)
			.forEach(kl -> kl.keyPressed(event));
	}
	
	private static void fireMouseClickEvent(MouseClickEvent event) {
		getListenersOfType(MouseClickListener.class)
			.forEach(mcl -> mcl.mouseClicked(event));
	}
	
	private static void fireMouseMotionEvent(MouseMotionEvent event) {
		getListenersOfType(MouseMotionListener.class)
			.forEach(mml -> mml.mouseMoved(event));
	}
	
	private static void fireMouseScrollEvent(MouseScrollEvent event) {
		getListenersOfType(MouseScrollListener.class)
			.forEach(msl -> msl.mouseScrolled(event));
	}
	
}
