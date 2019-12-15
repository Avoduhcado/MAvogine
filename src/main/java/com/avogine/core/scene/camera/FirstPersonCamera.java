package com.avogine.core.scene.camera;

import java.nio.DoubleBuffer;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import com.avogine.core.system.Input;
import com.avogine.core.system.Theater;
import com.avogine.core.system.event.KeyboardEvent;
import com.avogine.core.system.event.MouseClickEvent;
import com.avogine.core.system.event.MouseMotionEvent;
import com.avogine.core.system.event.MouseScrollEvent;
import com.avogine.core.system.listener.KeyboardListener;
import com.avogine.core.system.listener.MouseClickListener;
import com.avogine.core.system.listener.MouseMotionListener;
import com.avogine.core.system.listener.MouseScrollListener;

public class FirstPersonCamera extends Camera implements KeyboardListener, MouseMotionListener, MouseClickListener, MouseScrollListener {

	public FirstPersonCamera(Theater theater, Vector3f position, float speed, float yaw, float fov) {
		super(theater, position, speed, yaw, fov);
		Input.add(getContainer().getId(), this);
	}
	
	public FirstPersonCamera(Theater theater, Vector3f position) {
		this(theater, position, 10f, -90f, 45f);
	}
	
	public FirstPersonCamera(Theater theater) {
		this(theater, new Vector3f());
	}

	@Override
	public void mouseScrolled(MouseScrollEvent event) {
		if (getFov() >= 1.0f && getFov() <= 45.0f) {
			setFov(getFov() - event.getyOffset());
		}
		// TODO Put this clamp in setFov()
		if (getFov() <= 1.0f) {
			setFov(1.0f);
		} else if (getFov() >= 45.0f) {
			setFov(45.0f);
		}
	}

	@Override
	public void mouseClicked(MouseClickEvent event) {
		if (event.getType() != GLFW.GLFW_RELEASE) {
			return;
		}
		
		if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_1) {
			GLFW.glfwSetInputMode(event.getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
			try (MemoryStack stack = MemoryStack.stackPush()) {
				DoubleBuffer xPos = stack.mallocDouble(1);
				DoubleBuffer yPos = stack.mallocDouble(1);

				GLFW.glfwGetCursorPos(event.getWindow(), xPos, yPos);
				setLastX((float) xPos.get());
				setLastY((float) yPos.get());
			}
		} else if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_2) {
			GLFW.glfwSetInputMode(event.getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
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
		setPitch(getPitch() + yOffset);

		// Make sure that when pitch is out of bounds, screen doesn't get flipped
		if (getPitch() > 89.0f) {
			setPitch(89.0f);
		}
		if (getPitch() < -89.0f) {
			setPitch(-89.0f);
		}

		Vector3f front = new Vector3f();
		front.x = (float) (Math.cos(Math.toRadians(getYaw())) * Math.cos(Math.toRadians(getPitch())));
		front.y = (float) Math.sin(Math.toRadians(getPitch()));
		front.z = (float) (Math.sin(Math.toRadians(getYaw())) * Math.cos(Math.toRadians(getPitch())));
		getForward().set(front.normalize());
	}

	@Override
	public void keyPressed(KeyboardEvent event) {
		// TODO Replace the forward.cross computations with orientation?
		if (event.getType() == GLFW.GLFW_PRESS) {
			switch (event.getKey()) {
			case GLFW.GLFW_KEY_W:
				getVelocity().add(getForward());
				break;
			case GLFW.GLFW_KEY_A:
				getVelocity().sub(getForward().cross(getUp(), new Vector3f()).normalize());
				break;
			case GLFW.GLFW_KEY_S:
				getVelocity().sub(getForward());
				break;
			case GLFW.GLFW_KEY_D:
				getVelocity().add(getForward().cross(getUp(), new Vector3f()).normalize());
				break;
			case GLFW.GLFW_KEY_SPACE:
				getVelocity().add(getUp());
				break;
			case GLFW.GLFW_KEY_LEFT_SHIFT:
				getVelocity().sub(getUp());
				break;
			}
		}
	}

}
