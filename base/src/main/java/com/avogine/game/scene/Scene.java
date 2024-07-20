package com.avogine.game.scene;

import org.joml.Matrix4f;

/**
 * TODO Implement a custom Projection type to contain the projection matrix along with FOV, near/far planes.
 * TODO Implement some sort of Camera type to encapsulate the view matrix.
 */
public abstract class Scene {

	protected final Matrix4f projection;
	
	protected final Matrix4f view;
	
	/**
	 * 
	 */
	protected Scene() {
		this(new Matrix4f(), new Matrix4f());
	}
	
	protected Scene(Matrix4f projection, Matrix4f view) {
		this.projection = projection;
		this.view = view;
	}
	
	/**
	 * @return the projection matrix
	 */
	public Matrix4f getProjection() {
		return projection;
	}
	
	/**
	 * @return the view matrix
	 */
	public Matrix4f getView() {
		return view;
	}

}
