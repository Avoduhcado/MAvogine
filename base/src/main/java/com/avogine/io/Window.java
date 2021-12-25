package com.avogine.io;

import java.lang.invoke.*;
import java.nio.*;
import java.util.*;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.slf4j.*;

import com.avogine.*;
import com.avogine.experimental.annotation.*;

/**
 * {@link Window} provides the primary entry point into OpenGL and GLFW.
 * <p>
 * A new instance of {@code Window} needs to be initialized before any GL or GLFW calls can be made. This
 * should be handled from {@link Avogine} as one of the required parameters to start is a {@code Window}.
 * {@code Window}s can also optionally be registered with an {@link Input} for custom input handling as
 * any system events like key presses or mouse motions are captured from the window context.
 * <p>
 * TODO WindowOptions
 */
public class Window {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

	private long id;
	
	private int width;
	private int height;
	private String title;
	
	private int fps;
	
	private WindowOptions options;
	
	public static boolean debugMode;
		
	private List<Long> monitorList = new ArrayList<>();
	
	private Input input;
	
	/**
	 * @param width The width in pixels for this window
	 * @param height The height in pixels for this window
	 * @param title The window title
	 * @param options window specific settings for configuring OpenGL
	 */
	public Window(int width, int height, String title, WindowOptions options) {
		this.width = width;
		this.height = height;
		this.title = title;
		
		this.options = options;
	}
	
	/**
	 * @param input 
	 * 
	 */
	@InDev
	public void init(Input input) {
		GLFWErrorCallback.createPrint(System.err).set();
		
		if (!GLFW.glfwInit()) {
			throw new IllegalStateException("Could not initialize GLFW!");
		}

		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
		GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4);
//		GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
//		GLFW.glfwWindowHint(GLFW.GLFW_FLOATING, GLFW.GLFW_TRUE);
//		GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, GLFW.GLFW_TRUE);
		
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
		monitor = monitorList.get(options.monitorIndex);
		
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
		
		if (options.vsync) {
			GLFW.glfwSwapInterval(1);
		} else {
			GLFW.glfwSwapInterval(0);
		}
		
		if (GLFW.glfwGetWindowAttrib(id, GLFW.GLFW_TRANSPARENT_FRAMEBUFFER) == GLFW.GLFW_TRUE) {
			GLFW.glfwSetWindowOpacity(id, 0.5f);
		}
		
		GLFW.glfwShowWindow(id);

		GL.createCapabilities();
		
		// All of this should be handled through settings in WindowOptions
		GL11.glClearColor(options.clearColor[0], options.clearColor[1], options.clearColor[2], options.clearColor[3]);
		
		if (options.depthTest) {
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}

		if (options.blend) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
		
		if (options.cullFaces) {
			GL11.glEnable(GL11.GL_CULL_FACE);
			// TODO Fetch which face to cull from options as well
			GL11.glCullFace(GL11.GL_BACK);
		}

		if (options.multisample) {
			GL11.glEnable(GL13.GL_MULTISAMPLE);
		}
		
		GLFW.glfwSetFramebufferSizeCallback(id, (window, w, h) -> {
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
		
		if (GLFW.glfwRawMouseMotionSupported()) {
			GLFW.glfwSetInputMode(id, GLFW.GLFW_RAW_MOUSE_MOTION, GLFW.GLFW_TRUE);
		}
		
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
		
		this.input = input;
		input.init(this);
	}
	
	/**
	 * Swap the window's buffers to display the latest rendered frame and then process all pending events.
	 * <p>
	 * Callers should not need to handle setting the OpenGL context before rendering as this method will automatically check
	 * and, if needed, set this window context to be current.
	 */
	public void update() {
		if (GLFW.glfwGetCurrentContext() != id) {
			GLFW.glfwMakeContextCurrent(id);
		}
		
		GLFW.glfwSwapBuffers(id);
		GLFW.glfwPollEvents();
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
	 * TODO Convert to record
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
		public int monitorIndex = 0;
		
		public float[] clearColor = new float[] {100f / 255f, 149f / 255f, 237f / 255f, 1f};

		public boolean depthTest;
		
		public boolean cullFaces;
		
		public boolean blend;
		
		public boolean multisample;
		
	}
	
}
