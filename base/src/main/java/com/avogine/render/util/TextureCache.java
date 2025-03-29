package com.avogine.render.util;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

import org.lwjgl.BufferUtils;

import com.avogine.render.data.texture.*;

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
		return textureMap.computeIfAbsent(textureFile, Texture::new);
	}
	
	/**
	 * @param textureFiles
	 * @return
	 */
	public Cubemap getCubemap(String...textureFiles) {
		// XXX Might be better to standardize cubemap loading and instead reference a singular directory to load generic file names from for each side of the cube
		return (Cubemap) textureMap.computeIfAbsent(Arrays.asList(textureFiles).stream().collect(Collectors.joining(";")), Cubemap::new);
	}
	
	/**
	 * @param directory
	 * @param fileType
	 * @return
	 */
	public Cubemap getCubemap(String directory, String fileType) {
		return (Cubemap) textureMap.computeIfAbsent(directory, v -> new Cubemap(directory, fileType));
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
			return new Texture(64, 64, pixelBuffer);
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
