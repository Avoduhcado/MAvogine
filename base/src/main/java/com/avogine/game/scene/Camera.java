package com.avogine.game.scene;

import org.joml.*;

/**
 *
 */
public class Camera {

	private final Vector3f direction;
	private final Vector3f position;
	private final Vector2f rotation;
	private final Vector3f right;
	private final Vector3f up;
	
	private final Matrix4f viewMatrix;
	private final Matrix4f invertedViewMatrix;
	
	/**
	 * 
	 */
	public Camera() {
		direction = new Vector3f();
		position = new Vector3f();
		rotation = new Vector2f();
		right = new Vector3f();
		up = new Vector3f();
		
		viewMatrix = new Matrix4f();
		invertedViewMatrix = viewMatrix.invert(new Matrix4f());
	}
	
	/**
	 * @param x
	 * @param y
	 */
	public void addRotation(float x, float y) {
		rotation.add(x, y);
		recalculate();
	}

	/**
	 * @return the position
	 */
	public Vector3f getPosition() {
		return position;
	}

	/**
	 * @param inc The increment to move by.
	 */
	public void moveBackwards(float inc) {
		viewMatrix.positiveZ(direction).negate().mul(inc);
		position.sub(direction);
		recalculate();
	}

	/**
	 * @param inc The increment to move by.
	 */
	public void moveDown(float inc) {
		viewMatrix.positiveY(up).mul(inc);
		position.sub(up);
		recalculate();
	}

	/**
	 * @param inc The increment to move by.
	 */
	public void moveForward(float inc) {
		viewMatrix.positiveZ(direction).negate().mul(inc);
		position.add(direction);
		recalculate();
	}

	/**
	 * @param inc The increment to move by.
	 */
	public void moveLeft(float inc) {
		viewMatrix.positiveX(right).mul(inc);
		position.sub(right);
		recalculate();
	}

	/**
	 * @param inc The increment to move by.
	 */
	public void moveRight(float inc) {
		viewMatrix.positiveX(right).mul(inc);
		position.add(right);
		recalculate();
	}

	/**
	 * @param inc The increment to move by.
	 */
	public void moveUp(float inc) {
		viewMatrix.positiveY(up).mul(inc);
		position.add(up);
		recalculate();
	}

	private void recalculate() {
		viewMatrix.identity()
		.rotateX(rotation.x)
		.rotateY(rotation.y)
		.translate(-position.x, -position.y, -position.z);
	}

	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setPosition(float x, float y, float z) {
		position.set(x, y, z);
		recalculate();
	}

	/**
	 * @param x
	 * @param y
	 */
	public void setRotation(float x, float y) {
		rotation.set(x, y);
		recalculate();
	}
	
	/**
	 * @return the view
	 */
	public Matrix4f getView() {
		return viewMatrix;
	}
	
	/**
	 * @return the invertedView as it currently is, this will not update the matrix if {@link #invert()} has not already been called.
	 */
	public Matrix4f getInvertedView() {
		return invertedViewMatrix;
	}
	
	/**
	 * This will not modify the camera view, so should be the preferred way to retrieve an inverted result of this Camera view matrix.
	 * @return the result of calling {@link Matrix4f#invert()} on {@link #getView()}.
	 */
	public Matrix4f invert() {
		return viewMatrix.invert(invertedViewMatrix);
	}
	
}
