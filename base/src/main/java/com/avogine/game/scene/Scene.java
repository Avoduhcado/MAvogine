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
		this(new Matrix4f(), new Matrix4f());
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
	 * @param window The window this scene is being rendered to.
	 */
	public abstract void onRender(Window window);
	
	/**
	 * TODO Make abstract
	 * @param delta
	 */
	public void onUpdate(float delta) {
		
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
