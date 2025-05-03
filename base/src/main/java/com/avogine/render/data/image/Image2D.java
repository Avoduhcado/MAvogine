package com.avogine.render.data.image;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

/**
 * {@link ImageData} extension for explicitly 2D pixel data.
 */
public interface Image2D extends ImageData {

	/**
	 * Return the OpenGL internal format of the data.
	 * </p>
	 * This defaults to just return whatever {@link #format()} is set to.
	 * @return the OpenGL internal format of the data.
	 * @see GL11#glTexImage2D(int, int, int, int, int, int, int, int, ByteBuffer)
	 */
	public default int internalFormat() {
		return format();
	}

	/**
	 * @return the width of the image in pixels.
	 */
	public int width();
	
	/**
	 * @return the height of the image in pixels.
	 */
	public int height();
	
	/**
	 * @return the format of each pixel.
	 */
	public int format();
	
	/**
	 * @return the pixel buffer.
	 */
	public ByteBuffer pixels();
	
}
