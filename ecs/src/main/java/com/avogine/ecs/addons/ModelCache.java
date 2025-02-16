package com.avogine.ecs.addons;

import java.util.*;
import java.util.function.Supplier;

import com.avogine.ecs.*;
import com.avogine.render.TextureCache;
import com.avogine.render.data.Model;
import com.avogine.render.util.assimp.StaticModelLoader;

/**
 * TODO Should this be a singleton? Could be possible to support multiple model caches, but I'm not sure why.
 */
public class ModelCache implements EntitySystemAddon {

	private final Map<String, Model> modelMap;
	private final TextureCache textureCache;
	
	/**
	 * 
	 */
	public ModelCache() {
		modelMap = new HashMap<>();
		textureCache = new TextureCache();
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
		return modelMap.computeIfAbsent(modelFile, v -> StaticModelLoader.loadModel(modelFile, texturePath, textureCache));
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
	 * Convenience method for registering a {@link ModelCache} in the instance that one has not already been allocated.
	 * @param manager The {@link EntityManager} to register this {@link EntitySystemAddon} with.
	 * @return A Supplier to typically be used in conjunction with an {@link Optional#orElseGet(Supplier)} call to produce a new {@link ModelCache}.
	 */
	public static Supplier<ModelCache> registerModelCache(EntityManager manager) {
		return () -> (ModelCache) manager.registerAddon(new ModelCache());
	}
	
}
