package com.avogine.game.scene;

import org.joml.Matrix4f;

/**
 *
 */
public class Projection {
	
	private static final float DEFAULT_FOV = (float) Math.toRadians(60f);
	private static final float DEFAULT_Z_NEAR = 0.01f;
	private static final float DEFAULT_Z_FAR = 1000.0f;
	
	private final float fov;
	private final float zNear;
	private final float zFar;
	
	private final Matrix4f projectionMatrix;
	private final Matrix4f invertedProjectionMatrix;
	
	/**
	 * @param fov 
	 * @param width 
	 * @param height 
	 * @param zNear 
	 * @param zFar 
	 */
	public Projection(float fov, int width, int height, float zNear, float zFar) {
		this.fov = fov;
		projectionMatrix = new Matrix4f();
		invertedProjectionMatrix = new Matrix4f();
		this.zNear = zNear;
		this.zFar = zFar;
		updateProjectionMatrix(width, height);
	}
	
	/**
	 * @param width
	 * @param height
	 */
	public Projection(int width, int height) {
		this(DEFAULT_FOV, width, height, DEFAULT_Z_NEAR, DEFAULT_Z_FAR);
	}
	
	/**
	 * @return the projectionMatrix
	 */
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
	
	/**
	 * @return the invertedProjectionMatrix as it currently is, this will not update the matrix if {@link #invert()} has not already been called.
	 */
	public Matrix4f getInvertedProjectionMatrix() {
		return invertedProjectionMatrix;
	}
	
	/**
	 * This will not modify the projection matrix, so should be the preferred way to retrieve an inverted result of this projection matrix.
	 * @return the result of calling {@link Matrix4f#invert()} on {@link #getProjectionMatrix()}.
	 */
	public Matrix4f invert() {
		return projectionMatrix.invert(invertedProjectionMatrix);
	}
	
	/**
	 * @param width
	 * @param height
	 */
	public void updateProjectionMatrix(int width, int height) {
		projectionMatrix.setPerspective(fov, (float) width / height, zNear, zFar);
	}
	
}
