package com.avogine.render.data.texture;

import static org.lwjgl.opengl.GL11.*;

/**
 * Create a Texture Atlas with different texture sub regions contained within.
 * @param id
 * @param width The width of an individual cell in pixels
 * @param height The height of an individual cell in pixels
 * @param columns The number of columns contained in the atlas
 * @param rows The number of rows contained in the atlas
 */
public record TextureAtlas(int id, int width, int height, int columns, int rows) implements Texture {
	
	/**
	 * @param id
	 * @param width
	 * @param height
	 */
	public TextureAtlas(int id, int width, int height) {
		this(id, width, height, 1, 1);
	}
	
	/**
	 * @param id
	 */
	public TextureAtlas(int id) {
		this(id, 1, 1, 1, 1);
	}
	
	@Override
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, id);
	}
	
	@Override
	public void cleanup() {
		glDeleteTextures(id);
	}
	
}
