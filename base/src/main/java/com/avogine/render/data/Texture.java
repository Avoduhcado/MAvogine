package com.avogine.render.data;

import org.lwjgl.opengl.GL11;

/**
 *
 */
public class Texture {
	
	protected int id;
	
	/**
	 * @param id
	 */
	public Texture(int id) {
		this.id = id;
	}

	/**
	 * @return
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * 
	 */
	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		GL11.glDeleteTextures(id);
	}
	
}
