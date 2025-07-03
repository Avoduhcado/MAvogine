package com.avogine.render.image.data;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

/**
 * @param pixels 
 */
public record PixelBuffer(ByteBuffer pixels) implements PixelData {

	@Override
	public void close() {
		MemoryUtil.memFree(pixels);
	}

}
