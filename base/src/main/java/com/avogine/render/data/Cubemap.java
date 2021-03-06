package com.avogine.render.data;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

/**
 * A {@link TextureAtlas} wrapper that overrides specific texture binding operations to use cubemapping.
 * @author Dominus
 *
 */
public class Cubemap extends TextureAtlas {

	public Cubemap(int id) {
		super(id);
	}
	
	@Override
	public void bind() {
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, id);
	}

}
