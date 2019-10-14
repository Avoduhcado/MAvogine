package com.avogine.core.system;

import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class Theater {

	private long id;
	
	private int width;
	private int height;
	private String title;
	
	public Theater(int width, int height, String title) {
		this.width = width;
		this.height = height;
		this.title = title;
	}
	
	public void init() {
		GLFWErrorCallback.createPrint(System.err).set();
		
		if (!GLFW.glfwInit()) {
			throw new IllegalStateException("Could not initialize GLFW!");
		}
		
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
		
		id = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
		if (id == MemoryUtil.NULL) {
			throw new RuntimeException("Failed to create window!");
		}
		
		GLFW.glfwSetKeyCallback(id, (window, key, scancode, action, mods) -> {
			if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
				GLFW.glfwSetWindowShouldClose(window, true);
			}
		});
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);
			
			GLFW.glfwGetWindowSize(id, pWidth, pHeight);
			
			GLFWVidMode videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
			
			GLFW.glfwSetWindowPos(id, (videoMode.width() - pWidth.get(0)) / 2, (videoMode.height() - pHeight.get(0)) / 2);
		}
		
		GLFW.glfwMakeContextCurrent(id);
		
		GLFW.glfwShowWindow(id);

		GL.createCapabilities();
		GL11.glClearColor(100f / 255f, 149f / 255f, 237f / 255f, 1f);
		
		GLFW.glfwSetWindowSizeCallback(id, (window, width, height) -> {
			GL11.glViewport(0, 0, width, height);
		});
	}
	
	public void update() {
		GLFW.glfwSwapBuffers(id);
		GLFW.glfwPollEvents();
	}
	
	// XXX temporary convenience method
	public void setFps(int fps) {
		GLFW.glfwSetWindowTitle(id, title + " FPS: " + fps);
	}
	
	public long getId() {
		return id;
	}
	
	/**
	 * @return Window {@link #width} divided by {@link #height}
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
	
}
