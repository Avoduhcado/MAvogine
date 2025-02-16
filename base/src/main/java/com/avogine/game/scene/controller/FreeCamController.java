package com.avogine.game.scene.controller;

import org.joml.*;
import org.joml.Math;

import com.avogine.game.scene.Camera;

/**
 *
 */
public class FreeCamController {

	private final Camera camera;
	
	private final Vector3f position;
	private final Vector3f direction;
	private final Vector3f target;
	private final Vector3f up;
	private final Quaternionf orientation;
	private final Quaternionf rotationDelta;
	
	/**
	 * @param camera 
	 * @param position 
	 * @param orientation 
	 */
	public FreeCamController(Camera camera, Vector3f position, Quaternionf orientation) {
		this.camera = camera;
		this.position = new Vector3f().set(position);
		this.orientation = new Quaternionf().set(orientation);
		rotationDelta = new Quaternionf();
		direction = orientation.transformPositiveZ(new Vector3f());
		target = new Vector3f();
		up = orientation.transformPositiveY(new Vector3f());
		
		updateViewMatrix();
	}
	
	/**
	 * @param camera
	 * @param position
	 */
	public FreeCamController(Camera camera, Vector3f position) {
		this(camera, position, new Quaternionf());
	}
	
	/**
	 * @param camera
	 */
	public FreeCamController(Camera camera) {
		this(camera, new Vector3f());
	}
	
	/**
	 * Apply a lookAt transformation to the camera with the current position and orientation.
	 */
	public void updateViewMatrix() {
		camera.getView().setLookAt(position, orientation.transformPositiveZ(target).add(position), orientation.transformPositiveY(up));
		camera.invert();
	}
	
	/**
	 * @return the position
	 */
	public Vector3f getPosition() {
		return position;
	}
	
	/**
	 * @return the orientation
	 */
	public Quaternionf getOrientation() {
		return orientation;
	}
	
	/**
	 * Rotate the camera by the given rotation.
	 * @param rotation
	 */
	public void rotate(Quaternionf rotation) {
		orientation.mul(rotation);
		updateViewMatrix();
	}
	
	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public void rotate(float x, float y, float z) {
		//Use modulus to fix values to below 360 then convert values to radians
		float newX = Math.toRadians(x % 360);
		float newY = Math.toRadians(y % 360);
		float newZ = Math.toRadians(z % 360);

		//Create a quaternion with the delta rotation values
		rotationDelta.rotationXYZ(newX, newY, newZ).conjugate();

		//Multiply this transform by the rotation delta quaternion and its inverse
		orientation.mul(rotationDelta);
		
		updateViewMatrix();
	}

	/**
	 * @param delta
	 */
	public void moveForwards(float delta) {
		orientation.transformPositiveZ(direction).negate().mul(delta);
		position.add(direction);
		updateViewMatrix();
	}

	/**
	 * @param delta
	 */
	public void moveBackwards(float delta) {
		orientation.transformPositiveZ(direction).negate().mul(delta);
		position.sub(direction);
		updateViewMatrix();
	}

	/**
	 * @param delta
	 */
	public void strafeRight(float delta) {
		orientation.transformPositiveX(direction).mul(delta);
		position.add(direction);
		updateViewMatrix();
	}

	/**
	 * @param delta
	 */
	public void strafeLeft(float delta) {
		orientation.transformPositiveX(direction).mul(delta);
		position.sub(direction);
		updateViewMatrix();
	}
	
	/**
	 * @param delta
	 */
	public void moveUp(float delta) {
		orientation.transformPositiveY(direction).mul(delta);
		position.add(direction);
		updateViewMatrix();
	}
	
	/**
	 * @param delta
	 */
	public void moveDown(float delta) {
		orientation.transformPositiveY(direction).mul(delta);
		position.sub(direction);
		updateViewMatrix();
	}
	
}
