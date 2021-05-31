package com.avogine.render.data;

import org.lwjgl.opengl.GL11;

public class TextureAtlas {

	protected int id;

	protected int width;
	protected int height;
	
	protected int rows;
	protected int columns;

	/**
	 * Create a Texture Atlas with different texture sub regions contained within.
	 * @param id
	 * @param width The width of an individual cell in pixels
	 * @param height The height of an individual cell in pixels
	 * @param columns The number of columns contained in the atlas
	 * @param rows The number of rows contained in the atlas
	 */
	public TextureAtlas(int id, int width, int height, int columns, int rows) {
		this.id = id;
		this.width = width;
		this.height = height;
		this.rows = rows;
		this.columns = columns;
	}
	
	public TextureAtlas(int id, int width, int height) {
		this(id, width, height, 1, 1);
	}
	
	public TextureAtlas(int id) {
		this(id, 1, 1, 1, 1);
	}
	
	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
	}
	
	public int getId() {
		return id;
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getRows() {
		return rows;
	}
	
	public int getColumns() {
		return columns;
	}

	public void cleanup() {
		GL11.glDeleteTextures(id);
	}
	
}
