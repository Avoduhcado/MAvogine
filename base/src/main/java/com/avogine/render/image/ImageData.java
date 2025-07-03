package com.avogine.render.image;

import com.avogine.render.image.data.PixelData;

/**
 * Wrapper class for holding {@link PixelData} and image properties describing a render-able image.
 */
public class ImageData {

	private int width;
	private int height;
	private int format;
	private PixelData pixelBuffers;
	
	/**
	 * @param width
	 * @param height
	 * @param format
	 * @param pixelBuffers
	 */
	public ImageData(int width, int height, int format, PixelData pixelBuffers) {
		this.width = width;
		this.height = height;
		this.format = format;
		this.pixelBuffers = pixelBuffers;
	}
	
	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * @return the format
	 */
	public int getFormat() {
		return format;
	}
	
	/**
	 * @return the pixelBuffers
	 */
	public PixelData getPixelBuffers() {
		return pixelBuffers;
	}
	
}
