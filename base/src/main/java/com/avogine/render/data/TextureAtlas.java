package com.avogine.render.data;

/**
 * Create a Texture Atlas with different texture sub regions, tightly packed together with equal dimensions contained within.
 * @param id The OpenGL Texture ID.
 * @param width The width of the entire image in pixels..
 * @param height The height of the entire image in pixels.
 * @param columns The number of columns contained in the atlas.
 * @param rows The number of rows contained in the atlas.
 */
public record TextureAtlas(int id, int width, int height, int columns, int rows) implements Texture {
	
	public TextureAtlas(int id, int width, int height) {
		this(id, width, height, 1, 1);
	}
	
	public TextureAtlas(int id) {
		this(id, 1, 1);
	}

	/**
	 * @return The width in texture coordinates of a single cell of this atlas.
	 */
	public float getCellWidth() {
		return 1.0f / columns;
	}
	
	/**
	 * @return The height in texture coordinates of a single cell of this atlas.
	 */
	public float getCellHeight() {
		return 1.0f / rows;
	}
	
}
