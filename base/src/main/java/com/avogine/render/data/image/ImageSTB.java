package com.avogine.render.data.image;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;

/**
 * {@link Image2D} implementation with a pixel buffer allocated from {@link STBImage}.
 * @param width the width of the image in pixels.
 * @param height the height of the image in pixels.
 * @param format the format of each pixel.
 * @param pixels the buffer of pixels.
 */
public record ImageSTB(int width, int height, int format, ByteBuffer pixels) implements Image2D {

	@Override
	public void close() {
		STBImage.stbi_image_free(pixels);
	}

	@Override
	public int internalFormat() {
		return switch (format) {
			case GL11.GL_RGBA -> GL11.GL_RGBA8;
			default -> format;
		};
	}

}
