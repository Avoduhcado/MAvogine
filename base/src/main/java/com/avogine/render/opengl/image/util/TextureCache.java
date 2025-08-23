package com.avogine.render.opengl.image.util;

import java.util.*;

import com.avogine.render.opengl.Texture;

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
