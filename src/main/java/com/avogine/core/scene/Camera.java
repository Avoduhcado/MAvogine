package com.avogine.core.scene;

import org.joml.Vector3f;

import com.avogine.core.system.event.KeyboardEvent;
import com.avogine.core.system.event.MouseClickEvent;
import com.avogine.core.system.event.MouseEvent;
import com.avogine.core.system.event.MouseMotionEvent;
import com.avogine.core.system.event.MouseScrollEvent;
import com.avogine.core.system.listener.CameraController;

public class Camera {

	private Vector3f position;
	private Vector3f velocity;
	private float speed;
	
	private Vector3f forward = new Vector3f(0, 0, -1);
	private Vector3f up = new Vector3f(0, 1, 0);
	
	private float yaw;
	private float pitch;
	private float lastX;
	private float lastY;
	
	private float fov;
	
	private CameraController controller;
	
	public Camera(Vector3f position, float speed, float yaw, float fov) {
		this.position = position;
		this.velocity = new Vector3f();
		this.speed = speed;
		this.yaw = yaw;
		this.fov = fov;
	}
	
	public Camera(Vector3f position) {
		this(position, 10f, -90f, 45f);
	}
	
	public Camera() {
		this(new Vector3f());
	}
	
	public void update(float interval) {
		getVelocity().mul(getSpeed());
		
		getPosition().add(getVelocity().mul(interval));

		getVelocity().set(0);
	}
	
	public void processKeys(KeyboardEvent event) {
		if (controller == null) {
			return;
		}
		
		controller.keyPressed(event);
	}
	
	public void processMouse(MouseEvent event) {
		if (controller == null) {
			return;
		}
		
		if (event instanceof MouseClickEvent) {
			controller.mouseClicked((MouseClickEvent) event);
		} else if (event instanceof MouseMotionEvent) {
			controller.mouseMoved((MouseMotionEvent) event);
		} else if (event instanceof MouseScrollEvent) {
			controller.mouseScrolled((MouseScrollEvent) event);
		}
	}
	
	public void addCameraController(CameraController controller) {
		this.controller = (CameraController) SceneControl.add(controller);
	}

	public Vector3f getPosition() {
		return position;
	}
	
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	
	public Vector3f getVelocity() {
		return velocity;
	}
	
	public void setVelocity(Vector3f velocity) {
		this.velocity = velocity;
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public Vector3f getForward() {
		return forward;
	}
	
	public void setForward(Vector3f forward) {
		this.forward = forward;
	}
	
	public Vector3f getUp() {
		return up;
	}
	
	public void setUp(Vector3f up) {
		this.up = up;
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
	public float getLastX() {
		return lastX;
	}
	
	public void setLastX(float lastX) {
		this.lastX = lastX;
	}
	
	public float getLastY() {
		return lastY;
	}
	
	public void setLastY(float lastY) {
		this.lastY = lastY;
	}
	
	public float getFov() {
		return fov;
	}
	
	public void setFov(float fov) {
		this.fov = fov;
	}
	
}
