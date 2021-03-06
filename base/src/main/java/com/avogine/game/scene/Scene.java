package com.avogine.game.scene;

import org.joml.*;

import com.avogine.game.camera.*;
import com.avogine.io.*;

/**
 *
 */
public abstract class Scene {

	protected Matrix4f projection;
	protected Camera camera;
	
	protected Scene(Matrix4f projection, Camera camera) {
		this.projection = projection;
		this.camera = camera;
	}
	
	/**
	 * @param window
	 */
	public abstract void init(Window window);
	
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
