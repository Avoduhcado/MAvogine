package com.avogine.game.scene;

import org.joml.Matrix4f;

/**
 *
 */
public class Camera {

	private final Matrix4f viewMatrix;
	private final Matrix4f invertedViewMatrix;
	
	/**
	 *  
	 */
	public Camera() {
		viewMatrix = new Matrix4f();
		invertedViewMatrix = viewMatrix.invert(new Matrix4f());
	}
	
	/**
	 * This will not modify the camera view, so should be the preferred way to retrieve an inverted result of this Camera view matrix.
	 * @return the result of calling {@link Matrix4f#invert()} on {@link #getView()}.
	 */
	public Matrix4f invert() {
		return viewMatrix.invert(invertedViewMatrix);
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
	
}
