package com.avogine.game.scene.controller;

import org.joml.Math;
import org.joml.Vector3f;

import com.avogine.game.scene.Camera;
import com.avogine.util.MathUtil;

/**
 *
 */
public class FPSCameraController {

	private final Vector3f position;
	private final Vector3f target;
	
	private final Vector3f forward;
	private final Vector3f right;
	private final Vector3f up;
	private final Vector3f worldUp;

	/**
	 * x = Yaw
	 * y = Pitch
	 * z = Roll
	 */
	private final Vector3f rotation;
	private boolean constrainPitch;
	
	private final Camera camera;
	
	/**
	 * @param camera 
	 * @param position 
	 * @param up 
	 * @param yaw 
	 * @param pitch 
	 */
	public FPSCameraController(Camera camera, Vector3f position, Vector3f up, float yaw, float pitch) {
		this.camera = camera;
		this.position = new Vector3f().set(position);
		worldUp = up;
		target = new Vector3f();
		forward = new Vector3f();
		right = new Vector3f();
		this.up = new Vector3f();
		
		rotation = new Vector3f(yaw, pitch, 0);
		constrainPitch = true;
		updateVectors();
		
		updateViewMatrix();
	}
	
	/**
	 * @param camera 
	 * @param position 
	 */
	public FPSCameraController(Camera camera, Vector3f position) {
		this(camera, position, new Vector3f(0, 1, 0), 0f, 0f);
	}
	
	/**
	 * @param camera 
	 */
	public FPSCameraController(Camera camera) {
		this(camera, new Vector3f());
	}

	private void updateVectors() {
		forward.set(
				Math.cos(Math.toRadians(rotation.x)) * Math.cos(Math.toRadians(rotation.y)),
				Math.sin(Math.toRadians(rotation.y)),
				Math.sin(Math.toRadians(rotation.x)) * Math.cos(Math.toRadians(rotation.y)))
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
	 * @return the rotation
	 */
	public Vector3f getRotation() {
		return rotation;
	}
	
	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setRotation(float x, float y, float z) {
		if (constrainPitch) {
			y = MathUtil.clamp(y, -89.0f, 89.0f);
		}
		rotation.set(x, y, z);
		
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
			if (rotation.y + y > 89.0f) {
				y = 89.0f - rotation.y;
			} else if (rotation.y + y < -89.0f) {
				y = -89.0f - rotation.y;
			}
		}
		rotation.add(x, y, z);
		
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
		position.add(forward.mul(delta, new Vector3f()));
	}

	/**
	 * @param delta
	 */
	public void moveBackwards(float delta) {
		position.sub(forward.mul(delta, new Vector3f()));
	}

	/**
	 * @param delta
	 */
	public void strafeRight(float delta) {
		position.add(right.mul(delta, new Vector3f()));
	}

	/**
	 * @param delta
	 */
	public void strafeLeft(float delta) {
		position.sub(right.mul(delta, new Vector3f()));
	}
	
}
