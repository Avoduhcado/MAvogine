package com.avogine.render.loader.texture;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

import org.lwjgl.BufferUtils;

import com.avogine.render.data.*;

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
		return textureMap.computeIfAbsent(textureFile, v -> TextureLoader.loadTexture(textureFile));
	}
	
	/**
	 * @param textureFiles
	 * @return
	 */
	public CubemapTexture getCubemap(String...textureFiles) {
		// XXX Might be better to standardize cubemap loading and instead reference a singular directory to load generic file names from for each side of the cube
		return (CubemapTexture) textureMap.computeIfAbsent(Arrays.asList(textureFiles).stream().collect(Collectors.joining(" ")), v -> TextureLoader.loadCubemap(textureFiles));
	}
	
	/**
	 * @param directory
	 * @param fileType
	 * @return
	 */
	public CubemapTexture getCubemap(String directory, String fileType) {
		return (CubemapTexture) textureMap.computeIfAbsent(directory, v -> TextureLoader.loadCubemap(directory, fileType));
	}
	
	/**
	 * @return
	 */
	public Texture getDefaultTexture() {
		return textureMap.computeIfAbsent("DEFAULT__", v -> {
			ByteBuffer pixelBuffer = BufferUtils.createByteBuffer(64 * 64 * 4);
			while (pixelBuffer.hasRemaining()) {
				pixelBuffer.put((byte) 255);
				pixelBuffer.put((byte) 0);
				pixelBuffer.put((byte) 0);
				pixelBuffer.put((byte) 255);
			}
			pixelBuffer.flip();
			return TextureLoader.createEmptyTexture(64, 64, pixelBuffer);
		});
	}

	/**
	 * 
	 */
	public void cleanup() {
		textureMap.values().forEach(Texture::cleanup);
		textureMap.clear();
	}
	
}
