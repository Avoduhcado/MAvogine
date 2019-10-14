package com.avogine.loader.texture;

import java.util.HashMap;
import java.util.Map;

import com.avogine.core.render.Texture;

public class TextureCache {

	private Map<String, Texture> textureMap = new HashMap<>();
	
	private static TextureCache cache;
	
	public static TextureCache getInstance() {
		return cache;
	}

	public Texture getTexture(String textureFile) {
		// TODO Load texture if it does not already exist
		return textureMap.get(textureFile);
	}
	
}
