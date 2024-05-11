package com.avogine.io.event;

import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.IntBuffer;

import org.joml.*;
import org.lwjgl.system.MemoryStack;

/**
 *
 */
public non-sealed class MouseEvent extends InputEvent {

	public static final int MOUSE_PRESSED = 201;
	
	public static final int MOUSE_RELEASED = 202;
	
	public static final int MOUSE_CLICKED = 203;
	
	public static final int MOUSE_DRAGGED = 204;
	
	public static final int MOUSE_MOVED = 205;
	
	public static final int MOUSE_WHEEL = 206;
	
	public final int button;
	public final int clickCount;
	public float mouseX;
	public float mouseY;
	
	/**
	 * @param id
	 * @param button
	 * @param clickCount
	 * @param mouseX
	 * @param mouseY
	 * @param window
	 */
	public MouseEvent(int id, int button, int clickCount, float mouseX, float mouseY, long window) {
		this.id = id;
		this.button = button;
		this.clickCount = clickCount;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.window = window;
	}
	
	public int button() {
		return button;
	}
	
	public int clickCount() {
		return clickCount;
	}
	
	public float mouseX() {
		return mouseX;
	}
	
	public float mouseY() {
		return mouseY;
	}

	@Override
	public void consume() {
		consumed = true;
	}
	
	/**
	 * @param projection
	 */
	public void transformPoint(Matrix4f projection) {
		int displayWidth;
		int displayHeight;
		float halfWidth;
		float halfHeight;
		try (MemoryStack stack = stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);

			glfwGetFramebufferSize(window, w, h);
			displayWidth = w.get(0);
			displayHeight = h.get(0);

			halfWidth = displayWidth / 2.0f;
			halfHeight = displayHeight / 2.0f;
		}
		Vector3f transformedMouse = projection
				.invertOrtho(new Matrix4f())
				.transformPosition(mouseX, -mouseY, 0, new Vector3f())
				.div(halfWidth, halfHeight, 1f);
		mouseX = transformedMouse.x;
		mouseY = transformedMouse.y;
	}
}
