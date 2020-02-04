package com.avogine.core.scene.camera;

import java.nio.DoubleBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import com.avogine.core.entity.Entity;
import com.avogine.core.system.Input;
import com.avogine.core.system.Window;
import com.avogine.core.system.event.MouseClickEvent;
import com.avogine.core.system.event.MouseMotionEvent;
import com.avogine.core.system.event.MouseScrollEvent;
import com.avogine.core.system.listener.MouseClickListener;
import com.avogine.core.system.listener.MouseMotionListener;
import com.avogine.core.system.listener.MouseScrollListener;

/**
 * @author Dominus
 *
 */
public class ThirdPersonCamera extends Camera implements MouseScrollListener, MouseMotionListener, MouseClickListener {

	private Entity focus;
	private float radius;
	
	private boolean mouseOneDown;
	private boolean mouseTwoDown;
	
	/**
	 * @param window
	 * @param focus
	 */
	public ThirdPersonCamera(Window window, Entity focus) {
		super(window, new Vector3f(), 10, -90, 45f);
		this.focus = focus;
		this.radius = 25;
		Input.add(getContainer().getId(), this);
	}
	
	@Override
	public void update(float interval) {
		// TODO Focus on center or some other pre-defined focal point
		position.set(focus.getPosition().x, focus.getPosition().y + 1.5f, focus.getPosition().z);
		if (!mouseOneDown) {
			// TODO Set this to lerp to the position to avoid jolts
			float yaw = (float) -(Math.atan2(focus.getOrientation().y, focus.getOrientation().w) * 2.0f);
			setYaw((float) Math.toDegrees(yaw) + 180);
		}
	}
	
	@Override
	public Matrix4f getView() {
		view.identity();
		view.arcball(radius, position, (float) Math.toRadians(getPitch()), (float) Math.toRadians(getYaw()));
		return view;
	}
	
	@Override
	public void mouseScrolled(MouseScrollEvent event) {
		radius -= event.getyOffset();
		
		if (radius < 5) {
			radius = 5;
		} else if (radius > 50) {
			radius = 50;
		}
	}
	
	@Override
	public void mouseClicked(MouseClickEvent event) {
		switch (event.getType()) {
		case GLFW.GLFW_PRESS:
			try (MemoryStack stack = MemoryStack.stackPush()) {
				DoubleBuffer xPos = stack.mallocDouble(1);
				DoubleBuffer yPos = stack.mallocDouble(1);

				GLFW.glfwGetCursorPos(event.getWindow(), xPos, yPos);
				lastX = (float) xPos.get();
				lastY = (float) yPos.get();
			}
			switch (event.getButton()) {
			case GLFW.GLFW_MOUSE_BUTTON_1:
				mouseOneDown = true;
				break;
			case GLFW.GLFW_MOUSE_BUTTON_2:
				mouseTwoDown = true;
				break;
			}
			if (mouseOneDown || mouseTwoDown) {
				GLFW.glfwSetInputMode(event.getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
			}
			break;
		case GLFW.GLFW_RELEASE:
			switch (event.getButton()) {
			case GLFW.GLFW_MOUSE_BUTTON_1:
				mouseOneDown = false;
				break;
			case GLFW.GLFW_MOUSE_BUTTON_2:
				mouseTwoDown = false;
				break;
			}
			if (!mouseOneDown && !mouseTwoDown) {
				GLFW.glfwSetInputMode(event.getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
			}
			break;
		}
	}
	
	@Override
	public void mouseMoved(MouseMotionEvent event) {
		if (GLFW.glfwGetInputMode(event.getWindow(), GLFW.GLFW_CURSOR) != GLFW.GLFW_CURSOR_DISABLED) {
			return;
		}
		
		float xOffset = event.getXPosition() - lastX;
		float yOffset = lastY - event.getYPosition(); // reversed since y-coordinates go from bottom to top
		lastX = event.getXPosition();
		lastY = event.getYPosition();

		float sensitivity = 0.1f; // TODO Make this a customizable option
		xOffset *= sensitivity;
		yOffset *= sensitivity;

		yaw -= xOffset;
		pitch -= yOffset;

		// Make sure that when pitch is out of bounds, screen doesn't get flipped
		if (pitch > 89.0f) {
			pitch = 89.0f;
		}
		if (pitch < 0f) {
			pitch = 0f;
		}
		
//		if (!mouseOneDown) {
//			// TODO Set this to lerp to the position to avoid jolts
//			float yaw = (float) -(Math.atan2(focus.getOrientation().y, focus.getOrientation().w) * 2.0f);
//			yaw = (float) Math.toDegrees(yaw) + 180;
//		}
		
		if (mouseTwoDown) {
			focus.getOrientation().rotateY((float) Math.toRadians(xOffset));
		}
	}

}
