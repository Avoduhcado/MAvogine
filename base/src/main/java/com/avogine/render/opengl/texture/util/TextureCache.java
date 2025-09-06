package com.avogine.render.opengl.texture.util;

import java.util.*;

import com.avogine.render.opengl.texture.Texture;

/**
 *
 */
public class TextureCache {

	private final Map<String, Texture> textureMap;
	
	/**
	 * 
	 */
	public TextureCache() {
		textureMap = new HashMap<>();
	}

	/**
	 * TODO#57 Reference texture paths from a properties file rather than direct file paths so that you don't need to specify if its a 2D texture or a cubemap here.
	 * Not sure how that would affect model loading though, so maybe not a great idea. 
	 * @param textureFile
	 * @return
	 */
	public Texture getTexture(String textureFile) {
		return textureMap.computeIfAbsent(textureFile, TextureLoader::loadTexture);
	}
	
	/**
	 * @param posXTexturePath 
	 * @param negXTexturePath 
	 * @param posYTexturePath 
	 * @param negYTexturePath 
	 * @param posZTexturePath 
	 * @param negZTexturePath 
	 * @return
	 */
	public Texture getCubemap(String posXTexturePath, String negXTexturePath, String posYTexturePath, String negYTexturePath, String posZTexturePath, String negZTexturePath) {
		return textureMap.computeIfAbsent(posXTexturePath + negXTexturePath + posYTexturePath + negYTexturePath + posZTexturePath + negZTexturePath,
				v -> TextureLoader.loadCubemap(posXTexturePath, negXTexturePath, posYTexturePath, negYTexturePath, posZTexturePath, negZTexturePath));
	}
	
	/**
	 * @param directory
	 * @param fileType
	 * @return
	 */
	public Texture getCubemap(String directory, String fileType) {
		return textureMap.computeIfAbsent(directory, v -> TextureLoader.loadCubemap(directory, fileType));
	}
	
	/**
	 * @return
	 */
	public Texture getDefaultTexture() {
		return textureMap.computeIfAbsent("DEFAULT__", v -> TextureLoader.loadRawTexture(64, 64, (byte) 255, (byte) 0, (byte) 0, (byte) 255));
	}
	
	/**
	 * XXX Experimental, it may not be worth exposing the ability to manually submit textures to the cache, but currently this enables in memory model creation
	 * @param id
	 * @param texture
	 * @return
	 */
	public Texture putTexture(String id, Texture texture) {
		return textureMap.putIfAbsent(id, texture);
	}

	/**
	 * 
	 */
	public void cleanup() {
		textureMap.values().forEach(Texture::cleanup);
		textureMap.clear();
	}
	
}
