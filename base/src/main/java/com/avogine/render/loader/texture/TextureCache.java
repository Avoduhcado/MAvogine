package com.avogine.render.loader.texture;

import java.util.*;
import java.util.stream.Collectors;

import com.avogine.render.data.*;

/**
 *
 */
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
	
	public TextureAtlas getTextureAtlas(String textureFile, int columns, int rows) {
		return (TextureAtlas) textureMap.computeIfAbsent(textureFile, v -> TextureLoader.loadTextureAtlas(textureFile, columns, rows));
	}
	
	public Cubemap getCubemap(String...textureFiles) {
		// XXX Might be better to standardize cubemap loading and instead reference a singular directory to load generic file names from for each side of the cube
		return (Cubemap) textureMap.computeIfAbsent(Arrays.asList(textureFiles).stream().collect(Collectors.joining(" ")), v -> TextureLoader.loadCubemap(textureFiles));
	}
	
	public Cubemap getCubemap(String directory, String fileType) {
		return (Cubemap) textureMap.computeIfAbsent(directory, v -> TextureLoader.loadCubemap(directory, fileType));
	}

	public void cleanup() {
		textureMap.values().forEach(Texture::cleanup);
		textureMap.clear();
	}
	
}
