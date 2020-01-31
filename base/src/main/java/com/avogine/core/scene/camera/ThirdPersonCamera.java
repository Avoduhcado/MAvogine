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
 * Rotating and scrolling the mouse wheel together cause funkiness
 * @author Dominus
 *
 */
public class ThirdPersonCamera extends Camera implements MouseScrollListener, MouseMotionListener, MouseClickListener {

	private Entity focus;
	private float radius;
	
	private boolean mouseOneDown;
	private boolean mouseTwoDown;
	
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
			setYaw((float) Math.toDegrees(focus.getOrientation().angle()) + 180);
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
			GLFW.glfwSetInputMode(event.getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
			try (MemoryStack stack = MemoryStack.stackPush()) {
				DoubleBuffer xPos = stack.mallocDouble(1);
				DoubleBuffer yPos = stack.mallocDouble(1);

				GLFW.glfwGetCursorPos(event.getWindow(), xPos, yPos);
				setLastX((float) xPos.get());
				setLastY((float) yPos.get());
			}
			if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_1) {
				mouseOneDown = true;
			}
			break;
		case GLFW.GLFW_RELEASE:
			GLFW.glfwSetInputMode(event.getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
			if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_1) {
				mouseOneDown = false;
			}
			break;
		}
	}
	
	@Override
	public void mouseMoved(MouseMotionEvent event) {
		if (GLFW.glfwGetInputMode(event.getWindow(), GLFW.GLFW_CURSOR) != GLFW.GLFW_CURSOR_DISABLED) {
			return;
		}
		
		float xOffset = event.getXPosition() - getLastX();
		float yOffset = getLastY() - event.getYPosition(); // reversed since y-coordinates go from bottom to top
		setLastX(event.getXPosition());
		setLastY(event.getYPosition());

		float sensitivity = 0.1f; // TODO Make this a customizable option
		xOffset *= sensitivity;
		yOffset *= sensitivity;

		setYaw(getYaw() + xOffset);
		setPitch(getPitch() - yOffset);

		// Make sure that when pitch is out of bounds, screen doesn't get flipped
		if (getPitch() > 89.0f) {
			setPitch(89.0f);
		}
		if (getPitch() < 0f) {
			setPitch(0f);
		}
	}

}
