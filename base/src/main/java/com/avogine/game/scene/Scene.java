package com.avogine.game.scene;

import org.joml.*;

import com.avogine.game.*;
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
	public abstract Matrix4f getView();
	
}
