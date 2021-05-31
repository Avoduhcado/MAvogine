package com.avogine.game.camera;

import java.nio.*;

import org.lwjgl.glfw.*;
import org.lwjgl.system.*;

import com.avogine.io.event.*;
import com.avogine.io.listener.*;

/**
 *
 */
public class FirstPersonCameraController implements KeyboardListener, MouseMotionListener, MouseClickListener {

	private Camera camera;
	
	private float lastX;
	private float lastY;
	
	/**
	 * @param camera 
	 * 
	 */
	public FirstPersonCameraController(Camera camera) {
		this.camera = camera;
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
				lastX = (float) xPos.get();
				lastY = (float) yPos.get();
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
		
		float xOffset = event.getXPosition() - lastX;
		float yOffset = lastY - event.getYPosition(); // reversed since y-coordinates go from bottom to top
		lastX = event.getXPosition();
		lastY = event.getYPosition();

		float sensitivity = 0.1f; // TODO Make this a customizable mouse sensitivity option
		xOffset *= sensitivity;
		yOffset *= sensitivity;

		camera.getRotation().y += xOffset;
		camera.getRotation().x += yOffset;

		// Make sure that when pitch is out of bounds, screen doesn't get flipped
		if (camera.getRotation().x > 89.0f) {
			camera.getRotation().x = 89.0f;
		}
		if (camera.getRotation().x < -89.0f) {
			camera.getRotation().x = -89.0f;
		}

		camera.getForward().set(
				(float) (Math.cos(Math.toRadians(camera.getRotation().y)) * Math.cos(Math.toRadians(camera.getRotation().x))),
				(float) Math.sin(Math.toRadians(camera.getRotation().x)),
				(float) (Math.sin(Math.toRadians(camera.getRotation().y)) * Math.cos(Math.toRadians(camera.getRotation().x))))
		.normalize();
	}

	@Override
	public void keyPressed(KeyboardEvent event) {
		//		if (character != null && !character.onGround()) {
		//		jump = 0;
		//		return;
		//	}
		switch (event.getKey()) {
		case GLFW.GLFW_KEY_W:
			camera.getDirection().add(camera.getForward());
			break;
		case GLFW.GLFW_KEY_S:
			camera.getDirection().sub(camera.getForward());
			break;
		case GLFW.GLFW_KEY_A:
			camera.getDirection().sub(camera.getForward().cross(camera.getUp(), camera.getVelocity()).normalize());
			break;
		case GLFW.GLFW_KEY_D:
			camera.getDirection().add(camera.getForward().cross(camera.getUp(), camera.getVelocity()).normalize());
			break;
//		case GLFW.GLFW_KEY_E:
//			camera.getRotation().y = -1 * camera.getAngularSpeed();
//			break;
//		case GLFW.GLFW_KEY_Q:
//			camera.getRotation().y = camera.getAngularSpeed();
//			break;
		case GLFW.GLFW_KEY_SPACE:
			//		if (character != null && character.canJump()) {
			//			jump = 1;
			//		} else {
			//			jump = 0;
			//		}
			camera.getDirection().add(camera.getUp());
			break;
		case GLFW.GLFW_KEY_LEFT_SHIFT:
		case GLFW.GLFW_KEY_RIGHT_SHIFT:
			camera.getDirection().sub(camera.getUp());
			break;
		default:
			break;
		}
	}

	@Override
	public void keyReleased(KeyboardEvent event) {
		// TODO Auto-generated method stub
		
	}

}
