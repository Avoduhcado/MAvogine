package com.avogine.io;

import java.io.*;
import java.nio.*;
import java.util.*;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import com.avogine.*;
import com.avogine.experimental.annotation.*;
import com.avogine.logging.*;
import com.avogine.util.*;

/**
 * {@link Window} provides the primary entry point into OpenGL and GLFW.
 * <p>
 * A new instance of {@code Window} needs to be initialized before any GL or GLFW calls can be made. This
 * should be handled from {@link Avogine} as one of the required parameters to start is a {@code Window}.
 * {@code Window}s can also optionally be registered with an {@link Input} for custom input handling as
 * any system events like key presses or mouse motions are captured from the window context.
 * <p>
 * TODO WindowProperties
 */
public class Window {

	private long id;
	
	private int width;
	private int height;
	private String title;
	
	private int fps;
	
	private WindowProperties properties;
	
	public static boolean debugMode;
		
	private List<Long> monitorList = new ArrayList<>();
	
	private Input input;
	
	/**
	 * @param width The width in pixels for this window
	 * @param height The height in pixels for this window
	 * @param title The window title
	 */
	public Window(int width, int height, String title) {
		this.width = width;
		this.height = height;
		this.title = title;
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
		
		initProperties();
		
		long monitor = GLFW.glfwGetPrimaryMonitor();
		PointerBuffer monitorBuffer = GLFW.glfwGetMonitors();
		while (monitorBuffer.hasRemaining()) {
			long monitorID = monitorBuffer.get();
			monitorList.add(monitorID);
		}
		monitor = monitorList.get(properties.monitorIndex);
		
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
		
		if (properties.vsync) {
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
		GL11.glClearColor(properties.clearColor[0], properties.clearColor[1], properties.clearColor[2], properties.clearColor[3]);
		
		if (properties.depthTest) {
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}

		if (properties.blend) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
		
		if (properties.cullFaces) {
			GL11.glEnable(GL11.GL_CULL_FACE);
			// TODO Fetch which face to cull from options as well
			GL11.glCullFace(GL11.GL_BACK);
		}

		if (properties.multisample) {
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
			AvoLog.log().debug("x: {} y: {}", x, y);
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

	private void initProperties() {
		Properties prop = new Properties();
		try (var inputStream = getClass().getClassLoader().getResourceAsStream("window.properties")) {
			prop.load(inputStream);
		} catch (IOException e) {
			AvoLog.log().error("Error loading window properties", e);
		}
		properties = new WindowProperties(
				PropertiesUtil.getBoolean(prop, "fullscreen", false),
				PropertiesUtil.getBoolean(prop, "vsync", false),
				PropertiesUtil.getInteger(prop, "monitorIndex", 0),
				PropertiesUtil.getFloatArray(prop, "clearColor", new Float[] {100f / 255f, 149f / 255f, 237f / 255f, 1f}),
				PropertiesUtil.getBoolean(prop, "depthTest", true),
				PropertiesUtil.getBoolean(prop, "cullFaces", false),
				PropertiesUtil.getBoolean(prop, "blend", true),
				PropertiesUtil.getBoolean(prop, "multisample", true));
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
	 * A configurable set of parameters you can change to customize assorted OpenGL features.
	 * @param fullscreen Initialize the window in full screen mode.
	 * @param vsync Sync the window's refresh rate with the monitor's to avoid screen tearing.
	 * @param monitorIndex The default monitor that the window should be placed on.
	 * @param clearColor 4 float values determining the color to use when calling {@code glClear()}. <b>Probably shouldn't be in here</b>
	 * @param depthTest Enable depth testing when rendering to the screen. <b>Probably shouldn't be in here</b>
	 * @param cullFaces Perform face culling when rendering 3D objects. <b>Probably shouldn't be in here</b>
	 * @param blend Perform basic alpha blending when rendering. <b>Probably shouldn't be in here</b>
	 * @param multisample Perform multi-sample anti aliasing (MSAA) when rendering the screen.
	 */
	public static record WindowProperties(boolean fullscreen, boolean vsync, int monitorIndex, Float[] clearColor, boolean depthTest, boolean cullFaces, boolean blend, boolean multisample) {
		@Override
		public String toString() {
			return "WindowProperties [fullscreen=" + fullscreen + ", vsync=" + vsync + ", monitorIndex=" + monitorIndex
					+ ", clearColor=" + Arrays.toString(clearColor) + ", depthTest=" + depthTest + ", cullFaces="
					+ cullFaces + ", blend=" + blend + ", multisample=" + multisample + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(clearColor);
			result = prime * result
					+ Objects.hash(blend, cullFaces, depthTest, fullscreen, monitorIndex, multisample, vsync);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			WindowProperties other = (WindowProperties) obj;
			return blend == other.blend && Arrays.equals(clearColor, other.clearColor) && cullFaces == other.cullFaces
					&& depthTest == other.depthTest && fullscreen == other.fullscreen
					&& monitorIndex == other.monitorIndex && multisample == other.multisample && vsync == other.vsync;
		}
		
	}
	
}
