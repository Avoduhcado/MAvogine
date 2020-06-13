package com.avogine.game;

import java.lang.invoke.MethodHandles;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avogine.experimental.annotation.InDev;

/**
 * @author Dominus
 *
 */
public class Window {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

	private long id;
	
	private int width;
	private int height;
	private String title;
	
	private Timer timer;
	
	private int fps;
	private float frameTime;
	private int currentFrames;
	
	private WindowOptions options;
	
	public static boolean debugMode;
	
	private List<Long> monitorList = new ArrayList<>();
	
	/**
	 * @param width
	 * @param height
	 * @param title
	 * @param options
	 */
	public Window(int width, int height, String title, WindowOptions options) {
		this.width = width;
		this.height = height;
		this.title = title;
		
		this.options = options;
		timer = new Timer();
	}
	
	/**
	 * 
	 */
	@InDev
	public void init() {
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
//		GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
//		GLFW.glfwWindowHint(GLFW.GLFW_FLOATING, GLFW.GLFW_TRUE);
//		GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, GLFW.GLFW_TRUE);
		
		GLFWErrorCallback.createPrint(System.err).set();
		
		if (!GLFW.glfwInit()) {
			throw new IllegalStateException("Could not initialize GLFW!");
		}
		
		id = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
		if (id == MemoryUtil.NULL) {
			throw new RuntimeException("Failed to create window!");
		}
		
		long monitor = GLFW.glfwGetPrimaryMonitor();
		PointerBuffer monitorBuffer = GLFW.glfwGetMonitors();
		while (monitorBuffer.hasRemaining()) {
			long monitorID = monitorBuffer.get();
			monitorList.add(monitorID);
		}
		if (options != null) {
			monitor = monitorList.get(options.monitorIndex);
		}
		
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
		
		if (options != null && options.vsync) {
			GLFW.glfwSwapInterval(1);
		} else {
			GLFW.glfwSwapInterval(0);
		}
		
		if (GLFW.glfwGetWindowAttrib(id, GLFW.GLFW_TRANSPARENT_FRAMEBUFFER) == GLFW.GLFW_TRUE) {
			GLFW.glfwSetWindowOpacity(id, 0.5f);
		}
		
		GLFW.glfwShowWindow(id);

		GL.createCapabilities();
		
		GL11.glClearColor(100f / 255f, 149f / 255f, 237f / 255f, 1f);
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GLFW.glfwSetWindowSizeCallback(id, (window, w, h) -> {
			width = w;
			height = h;
			GL11.glViewport(0, 0, width, height);
			
//			try (MemoryStack stack = MemoryStack.stackPush()) {
//				FloatBuffer xscale = stack.mallocFloat(1);
//				FloatBuffer yscale = stack.mallocFloat(1);
//				
//				GLFW.glfwGetWindowContentScale(id, xscale, yscale);
//				
//				logger.debug("{} {}", xscale.get(), yscale.get());
//			}
		});
		
		GLFW.glfwSetWindowPosCallback(id, (window, x, y) -> {
			logger.debug("x: {} y: {}", x, y);
		});
		
//		Input.registerWindow(this);
		
		if (GLFW.glfwRawMouseMotionSupported()) {
			GLFW.glfwSetInputMode(id, GLFW.GLFW_RAW_MOUSE_MOTION, GLFW.GLFW_TRUE);
		}
		
		timer.init();
		
//		try (MemoryStack stack = MemoryStack.stackPush()) {
//			FloatBuffer scaleX = stack.mallocFloat(1);
//			FloatBuffer scaleY = stack.mallocFloat(1);
//			
//			GLFW.glfwGetMonitorContentScale(id, scaleX, scaleY);
//			
//			float contentScaleX = scaleX.get();
//			float contentScaleY = scaleY.get();
//			
//			logger.debug("{} {}", contentScaleX, contentScaleY);
//		}
	}
	
	/**
	 * Swap the window's buffers to display the latest rendered frame and then process all pending events.
	 * <p>
	 * Callers should not need to handle setting the OpenGL context before rendering as this method will automatically check
	 * and, if needed, set this window context to be current.
	 */
	public void render() {
		currentFrames++;
		if (frameTime >= 1.0f) {
			fps = currentFrames;
			frameTime = 0;
			currentFrames = 0;
			logger.debug("FPS: {}", fps);
		}
		
		if (GLFW.glfwGetCurrentContext() != id) {
			GLFW.glfwMakeContextCurrent(id);
		}
		GLFW.glfwSwapBuffers(id);
		GLFW.glfwPollEvents();
	}
	
	/**
	 * Sync rendering
	 */
	@InDev
	public void sync() {
		float loopSlot = 1f / Theater.TARGET_FPS;
		double endTime = timer.getLastLoopTime() + loopSlot;
		while (timer.getTime() < endTime) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException ie) {
				logger.error("Error during syncing", ie);
			}
		}
	}
	
	/**
	 * 
	 */
	public void update() {
		// XXX Minor hack to limit frame updates when the window is "held" and instead tie pauses to at least the target UPS
		float elapsedTime = Math.min(1.0f / Theater.TARGET_UPS, timer.getElapsedTime());
//		accumulator += elapsedTime;
		frameTime += elapsedTime;
//		logger.info("{}", frameTime);
//
//		while (accumulator >= interval) {
//			update(interval);
//			accumulator -= interval;
//		}
	}
	
	public void restoreState() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//		if (options.cullFace) {
//			GL11.glEnable(GL11.GL_CULL_FACE);
//			GL11.glCullFace(GL11.GL_BACK);
//		}
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
	
	public int getFps() {
		return fps;
	}
	
	public void setFps(int fps) {
		this.fps = fps;
	}
	
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
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	/**
	 * Assorted options to be set prior to window creation to control optional parameters.
	 */
	public static class WindowOptions {
		
		/**
		 * Initialize the window in full screen mode.
		 */
		public boolean fullScreen;
		
		/**
		 * Sync the window's refresh rate with the monitor's to avoid screen tearing.
		 */
		public boolean vsync;
		
		/**
		 * The default monitor that the window should be placed on.
		 */
		public int monitorIndex;
		
	}
	
}
