package com.avogine.game.scene;

import org.joml.*;

import com.avogine.io.*;

/**
 *
 */
public abstract class Scene {

	protected Matrix4f projection;
	
	/**
	 * 
	 */
	protected Scene() {
	}
	
	protected Scene(Matrix4f projection) {
		this.projection = projection;
	}
	
	/**
	 * @param window
	 */
	public abstract void init(Window window);
	
	/**
	 * An update method that runs once per game update.
	 * TODO Specify time unit
	 * @param delta the time between frames of the game update loop
	 */
	public abstract void update(float delta);
	
	/**
	 * @return the projection matrix
	 */
	public Matrix4f getProjection() {
		return projection;
	}
	
	/**
	 * @return the view matrix
	 */
	public abstract Matrix4f getView();
	
	/**
	 * 
	 */
	public abstract void cleanup();
	
}
