package com.avogine.io;

import static com.avogine.util.MathUtil.clamp;

import java.nio.IntBuffer;
import java.util.*;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.*;

import com.avogine.Avogine;
import com.avogine.game.ui.nuklear.NuklearUI;
import com.avogine.io.config.*;
import com.avogine.logging.AvoLog;
import com.avogine.render.loader.texture.IconLoader;
import com.avogine.util.resource.ResourceConstants;

/**
 * {@link Window} provides the primary entry point into OpenGL and GLFW.
 * <p>
 * A new instance of {@code Window} needs to be initialized before any GL or GLFW calls can be made. This
 * should be handled from {@link Avogine} as one of the required parameters to start is a {@code Window}.
 * {@code Window}s are also registered with an {@link Input} for custom input handling as any system events
 * like key presses or mouse motions are captured from the window context.
 * </p>
 */
public class Window {

	private long id;

	private String title;
	
	private int width;
	private int height;
	
	private boolean fullscreen;
	
	private int maxFps;
	private int maxBackgroundFps;
	private double targetFPS;
	private int fps;
	
	private boolean debugMode;
	
	private int monitorIndex;
	private List<Long> monitorList = new ArrayList<>();
	
	private final Input input;
	
	private final List<NuklearUI> guiContexts;
	
	/**
	 * @param title The window title
	 * @param preferences 
	 */
	public Window(String title, WindowPreferences preferences) {
		this.title = title;
		
		width = preferences.width();
		height = preferences.height();
		
		fullscreen = preferences.fullscreen();
		monitorIndex = preferences.monitor();
		
		maxFps = clamp(preferences.fps(), 0, 1000);
		maxBackgroundFps = clamp(preferences.backgroundFps(), 1, maxFps);
		
		input = new Input(this);
		
		guiContexts = new ArrayList<>();
	}
	
	/**
	 * @param windowConfig 
	 * @param inputConfig 
	 */
	public void init(GLFWConfig windowConfig, InputConfig inputConfig) {
		GLFWErrorCallback.createPrint().set();
		
		if (!GLFW.glfwInit()) {
			throw new IllegalStateException("Could not initialize GLFW!");
		}
		
		long monitor = GLFW.glfwGetPrimaryMonitor();
		PointerBuffer monitorBuffer = GLFW.glfwGetMonitors();
		while (monitorBuffer.hasRemaining()) {
			long monitorID = monitorBuffer.get();
			monitorList.add(monitorID);
		}
		monitor = monitorList.get(monitorIndex);
		
		windowConfig.windowHints().forEach(GLFW::glfwWindowHint);
		
		id = GLFW.glfwCreateWindow(width, height, title, fullscreen ? monitor : MemoryUtil.NULL, MemoryUtil.NULL);
		if (id == MemoryUtil.NULL) {
			throw new IllegalStateException("Failed to create window!");
		}
		
		GLFW.glfwSetFramebufferSizeCallback(id, (window, w, h) -> {
			width = w;
			height = h;
			GL11.glViewport(0, 0, width, height);
		});
		
		GLFW.glfwSetErrorCallback((int errorCode, long msgPtr) -> AvoLog.log().error("Error code [{}], msg [{}]", errorCode, MemoryUtil.memUTF8(msgPtr)));
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);
			
			IntBuffer pX = stack.mallocInt(1);
			IntBuffer pY = stack.mallocInt(1);
			
			GLFW.glfwGetWindowSize(id, pWidth, pHeight);
			GLFW.glfwGetMonitorPos(monitor, pX, pY);
			
			GLFWVidMode videoMode = GLFW.glfwGetVideoMode(monitor);
			
			GLFW.glfwSetWindowPos(id, pX.get() + (videoMode.width() - pWidth.get(0)) / 2, pY.get() + (videoMode.height() - pHeight.get(0)) / 2);
		}

		GLFW.glfwMakeContextCurrent(id);
		
		if (maxFps == 0) {
			GLFW.glfwSwapInterval(1);
		} else {
			GLFW.glfwSwapInterval(0);
			targetFPS = 1.0 / maxFps;
			GLFW.glfwSetWindowFocusCallback(id, (window, focused) -> targetFPS = 1.0 / (focused ? maxFps : maxBackgroundFps));
		}

		// TODO Customize by WindowConfig?
		if (GLFW.glfwGetWindowAttrib(id, GLFW.GLFW_TRANSPARENT_FRAMEBUFFER) == GLFW.GLFW_TRUE) {
			GLFW.glfwSetWindowOpacity(id, 1.0f);
		}
		
		IconLoader.loadAndSetIcons(id, ResourceConstants.TEXTURE_PATH + "icon");
		
		// TODO Customizable by WindowConfig?
		if (GLFW.glfwRawMouseMotionSupported()) {
			GLFW.glfwSetInputMode(id, GLFW.GLFW_RAW_MOUSE_MOTION, GLFW.GLFW_TRUE);
		}

		GLFW.glfwShowWindow(id);
		
		input.init(inputConfig);
	}
	
	/**
	 * Swap the window's buffers to display the latest rendered frame and then process all pending events.
	 * <p>
	 * Callers should not need to handle setting the OpenGL context before rendering as this method will automatically check
	 * and, if needed, set this window context to be current.
	 */
	public void swapBuffers() {
		if (GLFW.glfwGetCurrentContext() != id) {
			GLFW.glfwMakeContextCurrent(id);
		}
		
		GLFW.glfwSwapBuffers(id);
	}
	
	/**
	 * TODO Expose registerable callbacks for things like Nuklear to begin/end input polling around the glfwPollEvents call
	 */
	public void pollEvents() {
		guiContexts.forEach(NuklearUI::inputBegin);
		GLFW.glfwPollEvents();
		guiContexts.forEach(NuklearUI::inputEnd);
		
		input.update();
	}
	
	/**
	 * Register a {@link NuklearUI} to this Window so that it's internal input state can be processed around calls to {@link #pollEvents()}.
	 * @param gui The {@link NuklearUI} to register.
	 * @return The {@link NuklearUI}.
	 */
	public NuklearUI registerGUIContext(NuklearUI gui) {
		guiContexts.add(gui);
		return gui;
	}
	
	/**
	 * Unregister a {@link NuklearUI} from this Window.
	 * @param gui The {@link NuklearUI} to remove.
	 * @return {@code true} if the {@link NuklearUI} was removed.
	 */
	public boolean removeGUIContext(NuklearUI gui) {
		return guiContexts.remove(gui);
	}
	
	/**
	 * Return true if {@link GLFW#glfwWindowShouldClose} is true.
	 * @return true if {@code GLFW.glfwWindowShouldClose} is true
	 */
	public boolean shouldClose() {
		return GLFW.glfwWindowShouldClose(id);
	}
	
	/**
	 * Free all window callbacks and destroy this {@code Window}.
	 * <p>
	 * Use with caution, calling this will destroy the window even if {@link GLFW#glfwWindowShouldClose} is not
	 * true, so it is up to the user to handle removing all references to this window as attempting to use
	 * this window's context after it's been destroyed will fail.
	 */
	public void close() {
		Callbacks.glfwFreeCallbacks(id);
		GLFW.glfwDestroyWindow(id);
	}
	
	/**
	 * Close this window <b>and</b> terminate GLFW.
	 * <p>
	 * This should only be called when exiting the entire application.
	 */
	public void cleanup() {
		close();
		
		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
	}
	
	/**
	 * @return the input
	 */
	public Input getInput() {
		return input;
	}
	
	/**
	 * Return target frames per second. Controls how often the screen is rendered in a single second.
	 * </p>
	 * @return target frames per second. Controls how often the screen is rendered in a single second.
	 */
	public double getTargetFps() {
		return targetFPS;
	}
	
	/**
	 * @return An estimate value of how many frames are being rendered in this second based on elapsed time in the current game loop.
	 */
	public int getFps() {
		return fps;
	}
	
	/**
	 * @param fps
	 */
	public void setFps(int fps) {
		this.fps = fps;
	}
	
	/**
	 * @return The memory address of this {@link Window}.
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * Returns {@link #getWidth} / {@link #getHeight} as a {@code float}.
	 * @return {@code width / height} as a {@code float}
	 */
	public float getAspectRatio() {
		return (float) width / (float) height;
	}
	
	/**
	 * @return the width of the window in pixels
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * @return the height of the window in pixels
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * @param debugMode
	 */
	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}
	
	/**
	 * @return the debugMode
	 */
	public boolean isDebugMode() {
		return debugMode;
	}
	
}
