package com.avogine.render.data.texture;

import org.lwjgl.opengl.GL11;

/**
 *
 */
public interface Texture {
	
	/**
	 * 
	 */
	public void bind();

	/**
	 * @return The ID of the texture object to bind.
	 */
	public int id();

	/**
	 * Free any memory allocated by this Texture.
	 */
	public default void cleanup() {
		GL11.glDeleteTextures(id());
	}
	
}
