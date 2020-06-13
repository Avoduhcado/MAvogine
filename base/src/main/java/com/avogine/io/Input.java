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

import com.avogine.game.Theater;
import com.avogine.game.Window;
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
 * TODO Merge this with {@link Theater}
 * @author Dominus
 *
 */
public class Input {

	private static final Map<Long, Set<InputListener>> listeners;
	private static boolean[] keys;
	
	private static float lastMouseX;
	private static float lastMouseY;
	
	static {
		listeners = new HashMap<>();
		
		keys = new boolean[GLFW.GLFW_KEY_LAST];
		Arrays.fill(keys, false);
	}
	
	public static void registerWindow(Window register) {
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
	
	public static void update(Window window) {
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
	
	public static InputListener add(long id, InputListener l) {
		listeners.computeIfAbsent(id, v -> new HashSet<>()).add(l);
		return l;
	}
	
	public static InputListener add(Window window, InputListener l) {
		return add(window.getId(), l);
	}
	
	public static InputListener removeListener(long id, InputListener listener) {
		if (listeners.containsKey(id)) {
			listeners.get(id).remove(listener);
		}
		return listener;
	}

	public static InputListener removeListener(Window window, InputListener l) {
		return removeListener(window.getId(), l);
	}
	
	public static <T extends InputListener> Stream<T> getListenersOfType(long id, Class<T> clazz) {
		return listeners.getOrDefault(id, Set.of()).stream()
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
