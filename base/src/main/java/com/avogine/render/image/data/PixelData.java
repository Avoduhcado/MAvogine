package com.avogine.render.image.data;

import java.nio.ByteBuffer;

/**
 *
 */
public interface PixelData extends AutoCloseable {

	@Override
	public void close();
	
	/**
	 * @return
	 */
	public ByteBuffer pixels();
	
}
