package com.avogine.render.loader.assimp;

import java.util.*;

import com.avogine.ecs.EntitySystemAddon;
import com.avogine.render.data.mesh.Model;

/**
 *
 */
public class ModelCache implements EntitySystemAddon {

	private final Map<String, Model> modelMap;
	
	/**
	 * 
	 */
	public ModelCache() {
		modelMap = new HashMap<>();
	}
	
	/**
	 * @param modelName
	 * @return
	 */
	public boolean contains(String modelName) {
		return modelMap.containsKey(modelName);
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
		return modelMap.computeIfAbsent(modelFile, v -> StaticModelLoader.load(modelFile, texturePath));
	}
	
	/**
	 * @param modelName
	 * @param model
	 * @return
	 */
	public Model putModel(String modelName, Model model) {
		return modelMap.put(modelName, model);
	}
	
}
