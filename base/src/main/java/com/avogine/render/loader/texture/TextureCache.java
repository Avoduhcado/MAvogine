package com.avogine.render.loader.texture;

import java.util.*;

import com.avogine.render.data.TextureAtlas;

/**
 *
 */
public class TextureCache {

	private Map<String, TextureAtlas> textureMap = new HashMap<>();
	
	private static TextureCache cache;
	
	public static TextureCache getInstance() {
		if (cache == null) {
			cache = new TextureCache();
		}
		return cache;
	}

	public TextureAtlas getTexture(String textureFile) {
		return textureMap.computeIfAbsent(textureFile, v -> TextureLoader.loadTexture(textureFile));
	}
	
	public TextureAtlas getTextureAtlas(String textureFile, int columns, int rows) {
		return textureMap.computeIfAbsent(textureFile, v -> TextureLoader.loadTextureAtlas(textureFile, columns, rows));
	}
	
	public TextureAtlas getCubemap(String...textureFiles) {
		// XXX Might be better to standardize cubemap loading and instead reference a singular directory to load generic file names from for each side of the cube
		return textureMap.computeIfAbsent(Arrays.asList(textureFiles).stream().reduce((a, b) -> a + " " + b).get(), v -> TextureLoader.loadCubemap(textureFiles));
	}
	
	public TextureAtlas getCubemap(String directory, String fileType) {
		return textureMap.computeIfAbsent(directory, v -> TextureLoader.loadCubemap(directory, fileType));
	}

	public void cleanup() {
		textureMap.values().forEach(TextureAtlas::cleanup);
		textureMap.clear();
	}
	
}
