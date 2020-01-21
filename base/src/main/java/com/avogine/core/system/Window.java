package com.avogine.core.system;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class Window {

	private long id;
	
	private int width;
	private int height;
	private String title;
	
	private int fps;
	private Runnable fpsListener;
	
	private TheaterOptions options;
	
	public static boolean debugMode;
	
	private long monitor;
	private List<Long> monitorList = new ArrayList<>();
	
	public Window(int width, int height, String title, TheaterOptions options) {
		this.width = width;
		this.height = height;
		this.title = title;
		
		this.options = options;
	}
	
	public void init() {
		GLFWErrorCallback.createPrint(System.err).set();
		
		if (!GLFW.glfwInit()) {
			throw new IllegalStateException("Could not initialize GLFW!");
		}
		
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
//		GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
//		GLFW.glfwWindowHint(GLFW.GLFW_FLOATING, GLFW.GLFW_TRUE);
//		GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, GLFW.GLFW_TRUE);
		
		id = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
		if (id == MemoryUtil.NULL) {
			throw new RuntimeException("Failed to create window!");
		}
		
		monitor = GLFW.glfwGetPrimaryMonitor();
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
		
		if (GLFW.glfwGetWindowAttrib(id, GLFW.GLFW_TRANSPARENT_FRAMEBUFFER) == GLFW.GLFW_TRUE) {
			GLFW.glfwSetWindowOpacity(id, 0.5f);
		}
		
		GLFW.glfwShowWindow(id);

		GL.createCapabilities();
		
		GL11.glClearColor(100f / 255f, 149f / 255f, 237f / 255f, 1f);
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GLFW.glfwSetWindowSizeCallback(id, (window, width, height) -> {
			this.width = width;
			this.height = height;
			GL11.glViewport(0, 0, width, height);
			
//			try (MemoryStack stack = MemoryStack.stackPush()) {
//				FloatBuffer xscale = stack.mallocFloat(1);
//				FloatBuffer yscale = stack.mallocFloat(1);
//				
//				GLFW.glfwGetWindowContentScale(id, xscale, yscale);
//				
//				System.out.println(xscale.get() + " " + yscale.get());
//			}
		});
		
		GLFW.glfwSetWindowPosCallback(id, (window, x, y) -> {
			System.out.println("x " + x + " y " + y);
		});
		
		Input.registerWindow(this);
		
//		try (MemoryStack stack = MemoryStack.stackPush()) {
//			FloatBuffer scaleX = stack.mallocFloat(1);
//			FloatBuffer scaleY = stack.mallocFloat(1);
//			
//			GLFW.glfwGetMonitorContentScale(id, scaleX, scaleY);
//			
//			float contentScaleX = scaleX.get();
//			float contentScaleY = scaleY.get();
//			
//			System.out.println(contentScaleX + " " + contentScaleY);
//		}
	}
	
	/**
	 * Display the current frame to this window. If this window's context is not currently bound, bind it before attempting to draw the scene.
	 * After the scene buffer has been swapped this will poll for events, it should be the last thing called each frame.
	 */
	public void render() {
		if (GLFW.glfwGetCurrentContext() != id) {
			GLFW.glfwMakeContextCurrent(id);
		}
		GLFW.glfwSwapBuffers(id);
		GLFW.glfwPollEvents();
	}
	
	public void update() {
		
	}
	
	public void restoreState() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//		if (options.cullFace) {
//			glEnable(GL_CULL_FACE);
//			glCullFace(GL_BACK);
//		}
	}
	
	public int getFps() {
		return fps;
	}
	
	public void setFps(int fps) {
		this.fps = fps;
		if (fpsListener != null) {
			fpsListener.run();
		}
	}
	
	public void setFpsListener(Runnable run) {
		this.fpsListener = run;
	}
	
	public long getId() {
		return id;
	}
	
	/**
	 * Returns <tt>{@link #width} / {@link #height}</tt> as a <tt>float</tt>
	 * @return <tt>width / height</tt> as a <tt>float</tt>
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
	 * @author Dominus
	 *
	 */
	public static class TheaterOptions {
		
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
