package com.avogine.game.scene;

import org.joml.Matrix4f;

import com.avogine.game.Game;
import com.avogine.io.Window;

/**
 * 
 */
public abstract class Scene {

	protected final Matrix4f projection;
	
	protected final Matrix4f view;
	
	/**
	 * 
	 */
	protected Scene() {
		projection = new Matrix4f();
		view = new Matrix4f();
	}
	
	protected Scene(Matrix4f projection, Matrix4f view) {
		this.projection = projection;
		this.view = view;
	}
	
	/**
	 * @param game
	 * @param window
	 */
	public abstract void init(Game game, Window window);
	
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
