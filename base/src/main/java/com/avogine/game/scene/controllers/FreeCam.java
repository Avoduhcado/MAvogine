package com.avogine.game.scene.controllers;

import java.lang.Math;
import java.nio.DoubleBuffer;

import org.joml.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import com.avogine.entity.Transform;
import com.avogine.game.Game;
import com.avogine.game.util.*;
import com.avogine.io.event.*;
import com.avogine.io.listener.*;

/**
 *
 */
public class FreeCam implements KeyListener, MouseMotionListener, MouseButtonListener, Updateable {
	
	private static float FOV = 90f;
	private static float NEAR_PLANE = 0.1f;
	private static float FAR_PLANE = Float.POSITIVE_INFINITY;//15000.0f;
	
	private final Matrix4f projectionMatrix;
	private final Matrix4f cameraViewMatrix;
	
	private float lastX;
	private float lastY;
	
	private final Vector3f cameraVelocity;
	
	private final Vector3f inputDirection;
	private final Vector3f rotationalVelocity;
	
	private final Transform focusTransform;
	
	private float sensitivity = 1f;
	private boolean xInverted = false;
	private boolean yInverted = true;
	
	/**
	 * @param projection 
	 * @param view 
	 * 
	 */
	public FreeCam(Matrix4f projection, Matrix4f view) {
		projectionMatrix = projection;
		cameraViewMatrix = view;
		
		cameraVelocity = new Vector3f();
		focusTransform = new Transform();
		inputDirection = new Vector3f();
		rotationalVelocity = new Vector3f();
	}
	
	@Override
	public void onRegister(Game game) {
		float aspectRatio = game.getWindow().getAspectRatio();
		projectionMatrix.perspective((float) Math.toRadians(FOV), aspectRatio, NEAR_PLANE, FAR_PLANE);
		cameraViewMatrix.lookAlong(new Vector3f(0, 0, 1), new Vector3f(0, 1, 0));
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			DoubleBuffer xPos = stack.mallocDouble(1);
			DoubleBuffer yPos = stack.mallocDouble(1);

			GLFW.glfwGetCursorPos(game.getWindow().getId(), xPos, yPos);
			lastX = (float) xPos.get();
			lastY = (float) yPos.get();
		}
		
		game.getWindow().getInput().add(this);
	}
	
	@Override
	public void mouseClicked(MouseEvent event) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			DoubleBuffer xPos = stack.mallocDouble(1);
			DoubleBuffer yPos = stack.mallocDouble(1);

			GLFW.glfwGetCursorPos(event.window(), xPos, yPos);
			lastX = (float) xPos.get();
			lastY = (float) yPos.get();
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
	public void mouseMoved(MouseEvent event) {
		if (GLFW.glfwGetInputMode(event.window(), GLFW.GLFW_CURSOR) != GLFW.GLFW_CURSOR_DISABLED) {
			return;
		}
		
		float xOffset = lastX - event.mouseX();
		float yOffset = lastY - event.mouseY();
		lastX = event.mouseX();
		lastY = event.mouseY();
		
		var mouseDelta = new Vector2f(xInverted ? -xOffset : xOffset, yInverted ? -yOffset : yOffset).mul(sensitivity);
//		Quaternionf rotation = focusTransform.rotation();
//		Quaternionf horizontal = new Quaternionf(new AxisAngle4f((float) Math.toRadians(mouseDelta.x), new Vector3f(0, 1, 0)));
//		Quaternionf vertical = new Quaternionf(new AxisAngle4f((float) Math.toRadians(mouseDelta.y), new Vector3f(1, 0, 0)));
//		focusTransform.rotation().set(horizontal.mul(rotation).mul(vertical));
		rotationalVelocity.y += (xInverted ? -xOffset : xOffset) * sensitivity;
		rotationalVelocity.x += (yInverted ? -yOffset : yOffset) * sensitivity;
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent event) {
		// No implementation
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		switch (event.key()) {
		case GLFW.GLFW_KEY_W -> inputDirection.z += 1f;
		case GLFW.GLFW_KEY_S -> inputDirection.z += -1f;
		case GLFW.GLFW_KEY_A -> inputDirection.x += 1f;
		case GLFW.GLFW_KEY_D -> inputDirection.x += -1f;
		case GLFW.GLFW_KEY_SPACE -> inputDirection.y += 1f;
		case GLFW.GLFW_KEY_LEFT_CONTROL -> inputDirection.y -= 1f;
		case GLFW.GLFW_KEY_Q -> rotationalVelocity.z += 15f;
		case GLFW.GLFW_KEY_E -> rotationalVelocity.z += -15f;
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {
		// No implementation
	}

	@Override
	public void onUpdate(GameState gameState) {
		float delta = gameState.delta();

		if (rotationalVelocity.length() != 0) {
			var rotationQuaternion = new Quaternionf().fromAxisAngleDeg(rotationalVelocity, rotationalVelocity.length() * delta);
			focusTransform.rotation().mul(rotationQuaternion);
			rotationalVelocity.lerp(new Vector3f(), 15f * delta);
		}
		
		if (inputDirection.length() > 0) {
			inputDirection.normalize();
		}
		cameraVelocity.add(focusTransform.rotation().transform(inputDirection));
		inputDirection.zero();
		
		cameraVelocity.lerp(new Vector3f(), 5f * delta);
		focusTransform.translation().add(cameraVelocity.mul(delta, new Vector3f()));
		
		var target = new Vector3f(0, 0, 1);
		focusTransform.rotation().transform(target);
		target.add(focusTransform.translation());
		cameraViewMatrix.identity().lookAt(focusTransform.translation(), target, focusTransform.rotation().transformPositiveY(new Vector3f()));
	}

}
