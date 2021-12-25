package com.avogine.game.scene;

import org.joml.*;

import com.avogine.game.*;
import com.avogine.game.camera.*;
import com.avogine.io.*;

/**
 *
 */
public abstract class Scene {

	protected Matrix4f projection;
	protected Camera camera;
	
	/**
	 * 
	 */
	protected Scene() {
	}
	
	protected Scene(Matrix4f projection, Camera camera) {
		this.projection = projection;
		this.camera = camera;
	}
	
	/**
	 * @param window
	 */
	public abstract void init(Window window);
	
	/**
	 * An update method that runs once per game update.
	 * </p>
	 * Register this method on your {@link Game} object to handle
	 * automatically running Scene related updates.
	 * @param delta
	 */
	public abstract void onUpdate(float delta);
	
	/**
	 * @return the projection
	 */
	public Matrix4f getProjection() {
		return projection;
	}
	
	/**
	 * @return the camera
	 */
	public Camera getCamera() {
		return camera;
	}
	
	/**
	 * 
	 */
	public abstract void cleanup();
	
}
