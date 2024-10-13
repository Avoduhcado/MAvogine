package com.avogine.game.scene;

import org.joml.Matrix4f;

/**
 * 
 */
public abstract class Scene {

	protected final Projection projection;
	protected final Camera camera;
	
	/**
	 * 
	 */
	protected Scene(int width, int height) {
		this(new Projection(width, height), new Camera());
	}
	
	protected Scene(Projection projection, Camera camera) {
		this.projection = projection;
		this.camera = camera;
	}
	
	/**
	 * @return the projection
	 */
	public Projection getProjection() {
		return projection;
	}
	
	/**
	 * @return the projection matrix
	 */
	public Matrix4f getProjectionMatrix() {
		return projection.getProjectionMatrix();
	}
	
	/**
	 * @return the camera
	 */
	public Camera getCamera() {
		return camera;
	}
	
	/**
	 * @return the view matrix
	 */
	public Matrix4f getViewMatrix() {
		return camera.getView();
	}

}
