package com.avogine.render.data.texture;

import org.lwjgl.opengl.*;

/**
 * A {@link Texture} sub type that overrides specific texture binding operations to use cube mapping.
 * @param id The ID of the texture object to bind.
 */
public record TextureCubeMap(int id) implements Texture {

	@Override
	public void bind() {
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, id);
	}

}
