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
		return textureMap.computeIfAbsent(textureFile, v -> TextureLoader.loadTexture(textureFile));
	}
	
	public Texture getCubemap(String...textureFiles) {
		// XXX Might be better to standardize cubemap loading and instead reference a singular directory to load generic file names from for each side of the cube
		return textureMap.computeIfAbsent(Arrays.asList(textureFiles).stream().reduce((a, b) -> a + " " + b).get(), v -> TextureLoader.loadCubemap(textureFiles));
	}
	
	public Texture getCubemap(String directory) {
		return textureMap.computeIfAbsent(directory, v -> TextureLoader.loadCubemap(directory));
	}

	public void cleanup() {
		textureMap.values().forEach(Texture::cleanup);
	}
	
}
