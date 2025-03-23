package com.avogine.io.listener;

/**
 *
 */
public interface WindowResizeListener {

	/**
	 * This will be called from the main thread.
	 * </p>
	 * The window's OpenGL context should not be current on the main thread, as such
	 * calls to OpenGL context aware operations should be avoided.
	 * @param width
	 * @param height
	 */
	public void windowFramebufferResized(int width, int height);
	
}
