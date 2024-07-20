package com.avogine.render.data;

import org.lwjgl.opengl.*;

/**
 * A {@link Texture} implementation that overrides specific texture binding operations to use cubemapping.
 * @param id The OpenGL Texture ID.
 */
public record Cubemap(int id) implements Texture {

	@Override
	public void bind() {
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, id);
	}

}
