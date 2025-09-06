package com.avogine.render.image.data;

import java.nio.ByteBuffer;

import org.lwjgl.stb.STBImage;

/**
 * Wrapper class for holding a buffer of pixel data and image properties describing a render-able image.
 * @param width 
 * @param height 
 * @param channels 
 * @param pixels 
 */
public record ImageData(int width, int height, int channels, ByteBuffer pixels) implements AutoCloseable {

	@Override
	public void close() {
		STBImage.stbi_image_free(pixels);
	}
	
}
