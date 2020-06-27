package com.avogine.render;

import org.joml.Matrix4f;

import com.avogine.entity.Renderable;
import com.avogine.game.scene.Scene;
import com.avogine.render.shader.ShaderProgram;

/**
 * Base interface for handling scene rendering.
 * TODO Add abstract methods
 * @author Dominus
 * @param <T> 
 *
 */
public interface Renderer<T extends Renderable> {

	/**
	 * Initialize {@link ShaderProgram}s that will be in use by this {@code Renderer}.
	 */
	public void init();
	
	/**
	 * @param scene
	 * @param projection
	 * @param view
	 */
	public void renderScene(Scene<T> scene, Matrix4f projection, Matrix4f view);

	/**
	 * Cleanup each {@link ShaderProgram}.
	 */
	public void cleanup();
}
