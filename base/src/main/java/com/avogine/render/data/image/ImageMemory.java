package com.avogine.render.data.image;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

/**
 * {@link Image2D} implementation with a pixel buffer allocated from {@link MemoryUtil}.
 * @param width the width of the image in pixels.
 * @param height the height of the image in pixels.
 * @param format the format of each pixel.
 * @param pixels the buffer of pixels.
 */
public record ImageMemory(int width, int height, int format, ByteBuffer pixels) implements Image2D {

	@Override
	public void close() {
		MemoryUtil.memFree(pixels);
	}

}
