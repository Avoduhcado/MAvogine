package com.avogine.render.loader.assimp;

import java.util.*;

import com.avogine.render.data.mesh.Model;

/**
 *
 */
public class ModelCache {

	private final Map<String, Model> modelMap;
	
	private static ModelCache cache;
	
	/**
	 * @return a singleton instance of ModelCache.
	 */
	public static ModelCache getInstance() {
		if (cache == null) {
			cache = new ModelCache();
		}
		return cache;
	}
	
	/**
	 * 
	 */
	private ModelCache() {
		modelMap = new HashMap<>();
	}
	
	/**
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
	
	/**
	 * @param model
	 * @return
	 */
	public Model putModel(Model model) {
		return putModel(model.getName(), model);
	}
	
}
