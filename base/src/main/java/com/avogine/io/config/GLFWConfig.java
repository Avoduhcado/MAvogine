package com.avogine.io.config;

import java.util.Map;

import org.lwjgl.glfw.GLFW;

/**
 * @param windowHints 
 *
 */
public record GLFWConfig(Map<Integer, Integer> windowHints) {

	/**
	 * Return a default configuration of GLFW Window settings.
	 * 
	 * Targeting a basic OpenGL/GLFW configuration this will apply the following window hints:
	 * <ol>
	 * <li>GLFW_CONTEXT_VERSION_MAJOR: 3</li>
	 * <li>GLFW_CONTEXT_VERSION_MINOR: 3</li>
	 * <li>GLFW_VISIBLE: FALSE</li>
	 * <li>GLFW_RESIZABLE: TRUE</li>
	 * <li>GLFW_SAMPLES: 4</li>
	 * <li>GLFW_FOCUS_ON_SHOW: TRUE</li>
	 * </ol>
	 * 
	 * @return A default configuration of GLFW Window settings.
	 */
	public static GLFWConfig defaultConfig() {
		return new GLFWConfig(Map.of(
				GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3,
				GLFW.GLFW_CONTEXT_VERSION_MINOR, 3,
				GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE,
				GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE,
				GLFW.GLFW_SAMPLES, 4,
				GLFW.GLFW_FOCUS_ON_SHOW, GLFW.GLFW_TRUE));
//		GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
//		GLFW.glfwWindowHint(GLFW.GLFW_FLOATING, GLFW.GLFW_TRUE);
//		GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, GLFW.GLFW_TRUE);
	}
	
}
