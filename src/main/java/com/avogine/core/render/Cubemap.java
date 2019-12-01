package com.avogine.core.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

/**
 * A {@link Texture} wrapper that overrides specific texture binding operations to use cubemapping.
 * @author Dominus
 *
 */
public class Cubemap extends Texture {

	public Cubemap(int id) {
		super(id);
	}
	
	@Override
	public void bind() {
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, id);
	}
	
	@Override
	public void unbind() {
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, 0);
	}

}
