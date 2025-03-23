package com.avogine.io;

import java.nio.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.*;

import com.avogine.Avogine;
import com.avogine.game.util.Registerable;
import com.avogine.io.config.*;
import com.avogine.io.listener.WindowResizeListener;
import com.avogine.logging.AvoLog;
import com.avogine.util.ResourceUtils;
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
	private int fbWidth;
	private int fbHeight;
	
	private boolean fullscreen;
	private int monitorIndex;
	private List<Long> monitorList = new ArrayList<>();
	
	private boolean vsync;
	private int maxFps;
	private int maxBackgroundFps;
	private int targetFps;
	private int fps;
	
	private boolean debugMode;
	
	private final Input input;
	
	private final Queue<Registerable> queuedRegisters;
	
	private final List<WindowResizeListener> resizeListeners;
	
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
		
		vsync = preferences.vsync();
		maxFps = Math.clamp(preferences.fpsCap(), 1, 1000);
		maxBackgroundFps = Math.clamp(preferences.backgroundFps(), 1, maxFps);
		
		input = new Input(this);
		
		resizeListeners = new ArrayList<>();
		
		queuedRegisters = new ConcurrentLinkedQueue<>();
	}
	
	/**
	 * @param windowConfig 
	 * @param inputConfig 
	 */
	public void init(GLFWConfig windowConfig, InputConfig inputConfig) {
		GLFW.glfwSetErrorCallback((int errorCode, long msgPtr) -> AvoLog.log().error("Error code [{}], msg [{}]", errorCode, MemoryUtil.memUTF8(msgPtr)));
		
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
		
		GLFW.glfwSetFramebufferSizeCallback(id, (window, w, h) -> resized(w, h));
		
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
		
		targetFps = maxFps;
		
		// XXX Customize by WindowConfig?
		if (GLFW.glfwGetWindowAttrib(id, GLFW.GLFW_TRANSPARENT_FRAMEBUFFER) == GLFW.GLFW_TRUE) {
			GLFW.glfwSetWindowOpacity(id, 1.0f);
		}
		
		loadAndSetIcons(ResourceConstants.TEXTURES.with("icon", "AGDG Logo.png"));
		
		// XXX Customizable by WindowConfig?
		if (GLFW.glfwRawMouseMotionSupported()) {
			GLFW.glfwSetInputMode(id, GLFW.GLFW_RAW_MOUSE_MOTION, GLFW.GLFW_TRUE);
		}
		
		GLFW.glfwShowWindow(id);
		
		int[] arrWidth = new int[1];
		int[] arrHeight = new int[1];
		GLFW.glfwGetFramebufferSize(id, arrWidth, arrHeight);
		width = arrWidth[0];
		height = arrHeight[0];
		fbWidth = arrWidth[0];
		fbHeight = arrHeight[0];

		input.init(inputConfig);
		
		addRegisterable((Window window) -> GLFW.glfwSetWindowFocusCallback(id, (windowC, focused) -> 
			targetFps = focused ? maxFps : maxBackgroundFps
		));
	}
	
	/**
	 * 
	 */
	public void makeCurrent() {
		if (GLFW.glfwGetCurrentContext() != id) {
			GLFW.glfwMakeContextCurrent(id);
		}
	}
	
	/**
	 * Swap the window's buffers to display the latest rendered frame and then process all pending events.
	 * <p>
	 * Callers should not need to handle setting the OpenGL context before rendering as this method will automatically check
	 * and, if needed, set this window context to be current.
	 */
	public void swapBuffers() {
		makeCurrent();
		
		GLFW.glfwSwapBuffers(id);
	}
	
	/**
	 * Poll GLFW for events and process them in {@link Input}.
	 * @deprecated
	 */
	@Deprecated(since="0.1.3", forRemoval=true)
	public void pollEvents() {
		GLFW.glfwPollEvents();
	}
	
	/**
	 * 
	 */
	public void waitEvents() {
		while (!queuedRegisters.isEmpty()) {
			var register = queuedRegisters.poll();
			register.init(this);
		}
		
		// XXX Maybe use this with a timeout to target whatever the game's update loop interval is?
		GLFW.glfwWaitEvents();
	}
	
	/**
	 * 
	 * @param listener
	 * @return the {@link WindowResizeListener}
	 */
	public WindowResizeListener addResizeListener(WindowResizeListener listener) {
		resizeListeners.add(listener);
		return listener;
	}
	
	/**
	 * 
	 * @param listener
	 * @return true if the listener was removed.
	 */
	public boolean removeResizeListener(WindowResizeListener listener) {
		return resizeListeners.remove(listener);
	}
	
	/**
	 * @param register
	 * @return the register
	 */
	public Registerable addRegisterable(Registerable register) {
		queuedRegisters.add(register);
		return register;
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
		GLFW.glfwTerminate();
		Objects.requireNonNull(GLFW.glfwSetErrorCallback(null)).free();
	}
	
	private void resized(int width, int height) {
		this.width = width;
		this.height = height;
		this.fbWidth = width;
		this.fbHeight = height;
		resizeListeners.forEach(listener -> listener.windowFramebufferResized(width, height));
	}
	
	/**
	 * Read in image file data as {@link GLFWImage}s and load them as a Buffer to the Window icon.
	 * @param filePaths a list of image file resource paths to load as application icons.
	 */
	private void loadAndSetIcons(String...filePaths) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			Map<GLFWImage, ByteBuffer> iconBufferMap = new HashMap<>();
			
			for (String filePath : filePaths) {
				GLFWImage icon = GLFWImage.malloc(stack);

				IntBuffer iconWidth = stack.mallocInt(1);
				IntBuffer iconHeight = stack.mallocInt(1);
				IntBuffer nrChannels = stack.mallocInt(1);

				ByteBuffer fileData = ResourceUtils.readResourceToBuffer(filePath, 1024);
				ByteBuffer imageData = STBImage.stbi_load_from_memory(fileData, iconWidth, iconHeight, nrChannels, 0);
				if (imageData != null) {
					icon.set(iconWidth.get(), iconHeight.get(), imageData);
					iconBufferMap.put(icon, imageData);
				} else {
					AvoLog.log().warn("Icon failed to load: {}", filePath);
				}
			}
			
			GLFWImage.Buffer iconBuffer = GLFWImage.malloc(iconBufferMap.size(), stack);
			iconBufferMap.keySet().forEach(iconBuffer::put);
			iconBuffer.flip();
			GLFW.glfwSetWindowIcon(id, iconBuffer);
			iconBufferMap.values().forEach(MemoryUtil::memFree);
		}
	}
	
	/**
	 * @return the input
	 */
	public Input getInput() {
		return input;
	}
	
	/**
	 * Return maximum frames per second.
	 * </p>
	 * Controls how often the screen is rendered in a single second. This value may change when the window loses focus
	 * to allow for the application to consume less processing power in the background.
	 * @return the target maximum frames per second.
	 */
	public int getTargetFps() {
		return targetFps;
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
	 * @return the width of the main framebuffer in pixels.
	 */
	public int getFbWidth() {
		return fbWidth;
	}
	
	/**
	 * @return the height of the main framebuffer in pixels.
	 */
	public int getFbHeight() {
		return fbHeight;
	}
	
	/**
	 * @return true if VSync should be enabled.
	 */
	public boolean isVsync() {
		return vsync;
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
