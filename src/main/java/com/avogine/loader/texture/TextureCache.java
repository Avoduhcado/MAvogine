package com.avogine.loader.texture;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.avogine.core.render.Texture;

public class TextureCache {

	private Map<String, Texture> textureMap = new HashMap<>();
	
	private static TextureCache cache;
	
	public static TextureCache getInstance() {
		if (cache == null) {
			cache = new TextureCache();
		}
		return cache;
	}

	public Texture getTexture(String textureFile) {
		if (textureMap.containsKey(textureFile)) {
			return textureMap.get(textureFile);
		}
		Texture texture = TextureLoader.loadTexture(textureFile);
		if (texture != null) {
			textureMap.put(textureFile, texture);
		}
		return texture;
	}
	
	public Texture getCubemap(String...textureFiles) {
		// XXX Might be better to standardize cubemap loading and instead reference a singular directory to load generic file names from for each side of the cube
		String cubemapKey = Arrays.asList(textureFiles).stream().reduce((a, b) -> a + " " + b).get();
		if (textureMap.containsKey(cubemapKey)) {
			return textureMap.get(cubemapKey);
		}
		Texture texture = TextureLoader.loadCubemap(textureFiles);
		if (texture != null) {
			textureMap.put(cubemapKey, texture);
		}
		return texture;
	}
	
}
