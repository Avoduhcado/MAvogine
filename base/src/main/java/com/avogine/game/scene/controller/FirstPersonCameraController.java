package com.avogine.game.scene.controller;

import org.joml.Math;
import org.joml.Vector3f;

import com.avogine.game.scene.Camera;

/**
 *
 */
public class FirstPersonCameraController {

	private final Camera camera;
	
	private final Vector3f position;
	private final Vector3f target;
	
	private final Vector3f forward;
	private final Vector3f direction;
	private final Vector3f right;
	private final Vector3f up;
	private final Vector3f worldUp;
	
	private float yaw;
	private float pitch;
	private float roll;
	private boolean constrainPitch;
	
	/**
	 * @param camera 
	 * @param position 
	 * @param up 
	 * @param yaw 
	 * @param pitch 
	 */
	public FirstPersonCameraController(Camera camera, Vector3f position, Vector3f up, float yaw, float pitch) {
		this.camera = camera;
		
		this.position = new Vector3f().set(position);
		target = new Vector3f();
		
		forward = new Vector3f();
		direction = new Vector3f();
		right = new Vector3f();
		this.up = new Vector3f();
		worldUp = up;
		
		this.yaw = yaw;
		this.pitch = pitch;
		constrainPitch = true;
		
		updateVectors();
		updateViewMatrix();
	}
	
	/**
	 * @param camera 
	 * @param position 
	 */
	public FirstPersonCameraController(Camera camera, Vector3f position) {
		this(camera, position, new Vector3f(0, 1, 0), 0f, 0f);
	}
	
	/**
	 * @param camera 
	 */
	public FirstPersonCameraController(Camera camera) {
		this(camera, new Vector3f());
	}

	private void updateVectors() {
		forward.set(
				Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)),
				Math.sin(Math.toRadians(pitch)),
				Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)))
		.normalize();
		
		forward.cross(worldUp, right).normalize();
		right.cross(forward, up).normalize();
	}
	
	/**
	 * Set the view matrix to a "look at" transformation based on the current position, forward direction, and up vector.
	 * </p>
	 * This will additionally call {@link Camera#invert()}.
	 */
	public void updateViewMatrix() {
		camera.getView().setLookAt(position, position.add(forward, target), up);
		
		camera.invert();
	}

	/**
	 * @return the position
	 */
	public Vector3f getPosition() {
		return position;
	}

	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setPosition(float x, float y, float z) {
		position.set(x, y, z);
	}
	
	/**
	 * @return the target
	 */
	public Vector3f getTarget() {
		return target;
	}
	
	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setTarget(float x, float y, float z) {
		target.set(x, y, z);
	}

	/**
	 * @return the direction
	 */
	public Vector3f getForward() {
		return forward;
	}
	
	/**
	 * @return the right
	 */
	public Vector3f getRight() {
		return right;
	}
	
	/**
	 * @return the up
	 */
	public Vector3f getUp() {
		return up;
	}
	
	/**
	 * @return the yaw
	 */
	public float getYaw() {
		return yaw;
	}
	
	/**
	 * @return the pitch
	 */
	public float getPitch() {
		return pitch;
	}
	
	/**
	 * @return the roll
	 */
	public float getRoll() {
		return roll;
	}
	
	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setRotation(float x, float y, float z) {
		if (constrainPitch) {
			y = Math.clamp(y, -89.0f, 89.0f);
		}
		yaw = x;
		pitch = y;
		roll = z;
		
		updateVectors();
	}
	
	/**
	 * @param x
	 * @param y
	 */
	public void setRotation(float x, float y) {
		setRotation(x, y, 0);
	}
	
	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public void addRotation(float x, float y, float z) {
		if (constrainPitch) {
			if (pitch + y > 89.0f) {
				y = 89.0f - pitch;
			} else if (pitch + y < -89.0f) {
				y = -89.0f - pitch;
			}
		}
		yaw = x;
		pitch = y;
		roll = z;
		
		updateVectors();
	}
	
	/**
	 * @param x
	 * @param y
	 */
	public void addRotation(float x, float y) {
		addRotation(x, y, 0);
	}
	
	/**
	 * @param delta
	 */
	public void moveForwards(float delta) {
		position.add(forward.mul(delta, direction));
	}

	/**
	 * @param delta
	 */
	public void moveBackwards(float delta) {
		position.sub(forward.mul(delta, direction));
	}

	/**
	 * @param delta
	 */
	public void strafeRight(float delta) {
		position.add(right.mul(delta, direction));
	}

	/**
	 * @param delta
	 */
	public void strafeLeft(float delta) {
		position.sub(right.mul(delta, direction));
	}
	
}
