package com.avogine.render.data;

import org.lwjgl.opengl.*;

/**
 * A {@link Texture} sub type that overrides specific texture binding operations to use cubemapping.
 */
public class CubemapTexture extends Texture {
	
	/**
	 * @param id The OpenGL Texture ID.
	 */
	public CubemapTexture(int id) {
		super(id);
	}

	@Override
	public void bind() {
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, id);
	}

}
