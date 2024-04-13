package com.avogine.game.scene;

import java.util.*;

import org.joml.Matrix4f;

import com.avogine.game.Game;
import com.avogine.io.Window;
import com.avogine.render.data.experimental.*;

/**
 * 
 */
public abstract class Scene {

	protected final Matrix4f projection;
	
	protected final Matrix4f view;
	
	protected final Map<String, AModel> modelMap;
	protected final Map<String, AModel46> modelMap46;
	
	private AMaterialCache46 materialCache;
	
	/**
	 * 
	 */
	protected Scene() {
		this(new Matrix4f(), new Matrix4f());
	}
	
	protected Scene(Matrix4f projection, Matrix4f view) {
		this.projection = projection;
		this.view = view;
		this.modelMap = new HashMap<>();
		this.modelMap46 = new HashMap<>();
		materialCache = new AMaterialCache46();
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
	
	/**
	 * @return the modelMap
	 */
	public Map<String, AModel> getModelMap() {
		return modelMap;
	}
	
	/**
	 * @return the modelMap46
	 */
	public Map<String, AModel46> getModelMap46() {
		return modelMap46;
	}
	
	/**
	 * @return the materialCache
	 */
	public AMaterialCache46 getMaterialCache() {
		return materialCache;
	}
	
}
