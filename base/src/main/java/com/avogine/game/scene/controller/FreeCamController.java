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
	private final Quaternionf orientation;
	
	/**
	 * @param camera 
	 * @param position 
	 * @param orientation 
	 */
	public FreeCamController(Camera camera, Vector3f position, Quaternionf orientation) {
		this.camera = camera;
		this.position = new Vector3f().set(position);
		this.orientation = new Quaternionf().set(orientation);
		direction = orientation.transformPositiveZ(new Vector3f());
		target = new Vector3f();
		
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
	 * 
	 */
	public void updateViewMatrix() {
//		camera.getView().rotation(orientation).translate(position);
//		camera.getView().translation(position).rotate(orientation);
		
//		Vector3f euler = orientation.getEulerAnglesXYZ(new Vector3f());
//		camera.getView().identity().rotateX(euler.x).rotateY(euler.y).translate(-position.x, -position.y, -position.z);
//		camera.getView().rotationXYZ(euler.x, euler.y, euler.z);
		
//		camera.getView().translationRotateScale(position, orientation, 1);
		// TODO
		camera.getView().setLookAt(position, position.sub(orientation.transformPositiveZ(new Vector3f()), target), orientation.transformPositiveY(new Vector3f()));
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
		Quaternionf rotationDelta = new Quaternionf();
		rotationDelta.rotationXYZ(newX, newY, newZ).conjugate();

		//Calculate the inverse of the delta quaternion
//		Quaternionf conjugate = rotationDelta.conjugate();

		//Multiply this transform by the rotation delta quaternion and its inverse
		orientation.mul(rotationDelta);//.mul(conjugate);
		
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
