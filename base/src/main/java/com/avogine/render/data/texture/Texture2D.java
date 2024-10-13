package com.avogine.render.data.texture;

import org.lwjgl.opengl.GL11;

/**
 * The default implementation of {@link Texture} representing a 2D image loaded into GPU memory.
 * 
 * @param id The ID of the texture object to bind. 
 */
public record Texture2D(int id) implements Texture {

	@Override
	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
	}

}
