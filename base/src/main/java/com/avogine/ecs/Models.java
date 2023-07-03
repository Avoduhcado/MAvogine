package com.avogine.ecs;

import java.util.*;

import com.avogine.render.data.mesh.Model;
import com.avogine.render.loader.assimp.StaticModelLoader;

/**
 *
 */
public enum Models {

	/**
	 * A Singleton cache of model names to their respective {@link Model} instances.
	 */
	CACHE;
	
	private Map<String, Model> cache;
	
	Models() {
		cache = new HashMap<>();
	}
	
	/**
	 * @param modelName
	 * @return
	 */
	public boolean contains(String modelName) {
		return cache.containsKey(modelName);
	}
	
	/**
	 * TODO This could be converted to return some sort of Optional or CompletableFuture in the case that the model does not exist already so that it's then up to the
	 * caller if they would like to go along with immediately loading the model, or in the case that this is being called from render code where we may not want to start
	 * doing file IO it could defer the rendering and allow the loading to happen in the background.
	 * @param modelFile
	 * @param texturePath 
	 * @return
	 */
	public Model getModel(String modelFile, String texturePath) {
		return cache.computeIfAbsent(modelFile, v -> StaticModelLoader.load(modelFile, texturePath));
	}
	
	/**
	 * @param modelName
	 * @param model
	 * @return
	 */
	public Model putModel(String modelName, Model model) {
		return cache.put(modelName, model);
	}

}
