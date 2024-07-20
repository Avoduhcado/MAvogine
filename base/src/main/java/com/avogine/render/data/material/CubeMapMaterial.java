package com.avogine.render.data.material;

import org.lwjgl.opengl.*;

import com.avogine.render.data.TextureAtlas;

/**
 *
 */
public record CubeMapMaterial(TextureAtlas cube) implements Material {

	@Override
	public void bind() {
		if (cube != null) {
			GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, cube.id());
		}
	}

	@Override
	public void unbind() {
		// TODO Auto-generated method stub

	}

}
