package com.avogine.render.image.data;

import java.nio.ByteBuffer;

import org.lwjgl.stb.STBImage;

/**
 * @param pixels 
 */
public record STBPixelBuffer(ByteBuffer pixels) implements PixelData {

	@Override
	public void close() {
		STBImage.stbi_image_free(pixels);
	}
}
