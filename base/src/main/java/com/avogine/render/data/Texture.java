package com.avogine.render.data;

import org.lwjgl.opengl.GL11;

/**
 *
 */
public interface Texture {

	/**
	 * @return
	 */
	public int id();
	
	/**
	 * 
	 */
	public default void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id());
	}
	
	/**
	 * 
	 */
	public default void cleanup() {
		GL11.glDeleteTextures(id());
	}
	
}
