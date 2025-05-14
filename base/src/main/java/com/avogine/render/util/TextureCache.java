package com.avogine.render.util;

import java.util.*;

import com.avogine.render.data.gl.Texture;

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
		return textureMap.computeIfAbsent(textureFile, TextureBuilder::buildTexture);
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
				v -> TextureBuilder.buildCubemap(posXTexturePath, negXTexturePath, posYTexturePath, negYTexturePath, posZTexturePath, negZTexturePath));
	}
	
	/**
	 * @param directory
	 * @param fileType
	 * @return
	 */
	public Texture getCubemap(String directory, String fileType) {
		return textureMap.computeIfAbsent(directory, v -> TextureBuilder.buildCubemap(directory, fileType));
	}
	
	/**
	 * @return
	 */
	public Texture getDefaultTexture() {
		return textureMap.computeIfAbsent("DEFAULT__", v -> TextureBuilder.buildTexture(64, 64, (byte) 255, (byte) 0, (byte) 0, (byte) 255));
	}

	/**
	 * 
	 */
	public void cleanup() {
		textureMap.values().forEach(Texture::cleanup);
		textureMap.clear();
	}
	
}
