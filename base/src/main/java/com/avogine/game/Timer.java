package com.avogine.game;

import org.lwjgl.glfw.*;

/**
 * A wrapper class for managing calls to GLFW's time methods.
 */
public class Timer {

	/**
	 * Initialize the GLFW system timer.
	 * </p>
	 * This should largely be viewed as a convenience as the timer automatically gets initialized to 0
	 * when {@link GLFW#glfwInit()} is called.
	 */
	public void init() {
		GLFW.glfwSetTime(0.0);
	}

	/**
	 * Returns the current time in seconds since {@link #init()} was called.
	 * @return the current time in seconds since {@link #init()} was called.
	 */
	public double getTime() {
		return GLFW.glfwGetTime();
	}

}
