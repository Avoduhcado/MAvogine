package com.avogine.render.data;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

import com.avogine.render.data.texture.Texture;

/**
 * A {@link Texture} implementation that overrides specific texture binding operations to use cubemapping.
 * @param id The ID of the texture in OpenGL.
 *
 */
public record Cubemap(int id) implements Texture {

	@Override
	public void bind() {
		glBindTexture(GL_TEXTURE_CUBE_MAP, id);
	}

	@Override
	public void cleanup() {
		glDeleteTextures(id);
	}

}
