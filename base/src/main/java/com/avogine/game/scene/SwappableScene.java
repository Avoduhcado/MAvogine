package com.avogine.game.scene;

import org.joml.Matrix4f;

import com.avogine.game.util.SceneState;

/**
 *
 */
public abstract class SwappableScene extends Scene {

	/**
	 * @param projection 
	 * @param view 
	 * 
	 */
	protected SwappableScene(Matrix4f projection, Matrix4f view) {
		super(projection, view);
	}
	
	/**
	 * @return
	 */
	public abstract SceneState getSceneState();
	
}
