/**
 * 
 */
package com.avogine.game;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import com.avogine.experimental.annotation.InDev;

/**
 * @author Dominus
 *
 */
@InDev
public class Theater {

	/**
	 * Target frames per second. Controls how often the screen is rendered in a single second.
	 */
	public static final int TARGET_FPS = 60;

	/**
	 * Target updates per second. Controls how often game logic is run in a single second.
	 */
	public static final int TARGET_UPS = 60;
	
	protected final List<Window> windows = new ArrayList<>();
	
	/**
	 * Create a new {@code Theater} without any {@link Window}s attached.
	 */
	public Theater() {
		
	}
	
	/**
	 * Create a new {@code Theater} with a {@link Window} attached.
	 * <p>
	 * The attached {@code Window} will not be initialized until {@link #init()} is called.
	 * @param window The {@code Window} to instantiate {@code Theater} with.
	 */
	public Theater(Window window) {
		windows.add(window);
	}
	
	/**
	 * Initialize GLFW and set up the error callback stream.
	 * <p>
	 * {@code GLFW.glfwInit} must be called here rather than the constructor so that it's initialized on the
	 * {@code Play.gameLoopThread}. If any {@link Window}s have already been registered this will also handle
	 * initializing them for the same reason.
	 * <p>
	 * This method can be overridden for additional initializations, but {@code super()} should always be called.
	 */
	public void init() {
		GLFWErrorCallback.createPrint(System.err).set();
		
		if (!GLFW.glfwInit()) {
			throw new IllegalStateException("Could not initialize GLFW!");
		}
		
		windows.forEach(Window::init);
	}
	
	/**
	 * Perform per window game loop
	 * TODO
	 */
	public void update() {
		windows.forEach(window -> {
			window.update();
			window.render();
			window.sync();
		});
		windows.removeAll(windows.stream()
				.filter(Window::shouldClose)
				.peek(Window::close)
				.collect(Collectors.toList()));
	}

	/**
	 * Called once per game loop to determine if the process should terminate.
	 * <p>
	 * Override this to provide custom game control, ie. if you want the game to keep running even when no
	 * {@code Windows} are left to display.
	 * @return true if there are no registered {@link Window}s
	 */
	public boolean shouldClose() {
		return windows.isEmpty();
	}
	
	/**
	 * Add a {@link Window} to the screen and immediately initialize it so that it is ready to render.
	 * @param window the {@code Window} to register
	 */
	public void registerWindow(Window window) {
		windows.add(window);
		window.init();
	}
	
	/**
	 * A {@code List} of all currently registered {@link Window}s.
	 * @return a {@code List} of all currently registered {@code Window}s
	 */
	public List<Window> getWindows() {
		return windows;
	}

}
