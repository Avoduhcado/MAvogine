package com.avogine.game.camera;

import java.nio.DoubleBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import com.avogine.io.Window;
import com.avogine.io.event.*;
import com.avogine.io.listener.*;

/**
 * TODO Delete or update to use scene.Camera
 */
public class FirstPersonCameraController implements KeyListener, MouseMotionListener, MouseButtonListener {

	private Camera camera;
	
	private float lastX;
	private float lastY;
	
	private boolean flymode;
	
	/**
	 * @param camera 
	 * @param window 
	 * 
	 */
	public FirstPersonCameraController(Camera camera, Window window) {
		this.camera = camera;
		
		GLFW.glfwSetInputMode(window.getId(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
		try (MemoryStack stack = MemoryStack.stackPush()) {
			DoubleBuffer xPos = stack.mallocDouble(1);
			DoubleBuffer yPos = stack.mallocDouble(1);

			GLFW.glfwGetCursorPos(window.getId(), xPos, yPos);
			lastX = (float) xPos.get();
			lastY = (float) yPos.get();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent event) {
		if (event.button() == GLFW.GLFW_MOUSE_BUTTON_1) {
			GLFW.glfwSetInputMode(event.window(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
			lastX = event.mouseX();
			lastY = event.mouseY();
		} else if (event.button() == GLFW.GLFW_MOUSE_BUTTON_2) {
			GLFW.glfwSetInputMode(event.window(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
		}
	}

	@Override
	public void mouseMoved(MouseEvent event) {
		if (GLFW.glfwGetInputMode(event.window(), GLFW.GLFW_CURSOR) != GLFW.GLFW_CURSOR_DISABLED) {
			return;
		}
		
		float xOffset = event.mouseX() - lastX;
		float yOffset = lastY - event.mouseY(); // reversed since y-coordinates go from bottom to top
		lastX = event.mouseX();
		lastY = event.mouseY();

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
	public void keyPressed(KeyEvent event) {
		//		if (character != null && !character.onGround()) {
		//		jump = 0;
		//		return;
		//	}
		switch (event.key()) {
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
		case GLFW.GLFW_KEY_LEFT_SHIFT,
			GLFW.GLFW_KEY_RIGHT_SHIFT:
			camera.getDirection().sub(camera.getUp());
			break;
		default:
			break;
		}
		if (!flymode) {
			camera.getDirection().y = 0;
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {
		
	}

	@Override
	public void keyTyped(KeyEvent event) {
		switch (event.key()) {
		case GLFW.GLFW_KEY_LEFT_CONTROL:
			flymode = !flymode;
			break;
		default:
			break;
		}
	}

	@Override
	public void mousePressed(MouseEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		// TODO Auto-generated method stub
		
	}

}
