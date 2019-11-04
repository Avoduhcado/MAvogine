package com.avogine.core.system.listener;

import java.nio.DoubleBuffer;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import com.avogine.core.scene.Camera;
import com.avogine.core.system.event.KeyboardEvent;
import com.avogine.core.system.event.MouseClickEvent;
import com.avogine.core.system.event.MouseMotionEvent;
import com.avogine.core.system.event.MouseScrollEvent;

// TODO Replace GLFW_KEYs with custom key bindings

public class FirstPersonCameraController implements CameraController {

	private Camera camera;
	
	public FirstPersonCameraController(Camera camera) {
		this.camera = camera;
	}
	
	@Override
	public void keyPressed(KeyboardEvent event) {
		if (event.getType() == GLFW.GLFW_PRESS) {
			switch (event.getKey()) {
			case GLFW.GLFW_KEY_W:
				camera.getVelocity().add(camera.getForward());
				break;
			case GLFW.GLFW_KEY_A:
				camera.getVelocity().sub(camera.getForward().cross(camera.getUp(), new Vector3f()).normalize());
				break;
			case GLFW.GLFW_KEY_S:
				camera.getVelocity().sub(camera.getForward());
				break;
			case GLFW.GLFW_KEY_D:
				camera.getVelocity().add(camera.getForward().cross(camera.getUp(), new Vector3f()).normalize());
				break;
			case GLFW.GLFW_KEY_SPACE:
				camera.getVelocity().add(camera.getUp());
				break;
			case GLFW.GLFW_KEY_LEFT_SHIFT:
				camera.getVelocity().sub(camera.getUp());
				break;
			}
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
				camera.setLastX((float) xPos.get());
				camera.setLastY((float) yPos.get());
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
		
		float xOffset = event.getXPosition() - camera.getLastX();
		float yOffset = camera.getLastY() - event.getYPosition(); // reversed since y-coordinates go from bottom to top
		camera.setLastX(event.getXPosition());
		camera.setLastY(event.getYPosition());

		float sensitivity = 0.1f; // change this value to your liking
		xOffset *= sensitivity;
		yOffset *= sensitivity;

		camera.setYaw(camera.getYaw() + xOffset);
		camera.setPitch(camera.getPitch() + yOffset);

		// make sure that when pitch is out of bounds, screen doesn't get flipped
		if (camera.getPitch() > 89.0f) {
			camera.setPitch(89.0f);
		}
		if (camera.getPitch() < -89.0f) {
			camera.setPitch(-89.0f);
		}

		Vector3f front = new Vector3f();
		front.x = (float) (Math.cos(Math.toRadians(camera.getYaw())) * Math.cos(Math.toRadians(camera.getPitch())));
		front.y = (float) Math.sin(Math.toRadians(camera.getPitch()));
		front.z = (float) (Math.sin(Math.toRadians(camera.getYaw())) * Math.cos(Math.toRadians(camera.getPitch())));
		camera.getForward().set(front.normalize());
	}

	@Override
	public void mouseScrolled(MouseScrollEvent event) {
		if (camera.getFov() >= 1.0f && camera.getFov() <= 45.0f) {
			camera.setFov(camera.getFov() - event.getyOffset());
		}
		if (camera.getFov() <= 1.0f) {
			camera.setFov(1.0f);
		} else if (camera.getFov() >= 45.0f) {
			camera.setFov(45.0f);
		}
	}

}
