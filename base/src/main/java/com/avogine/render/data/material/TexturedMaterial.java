package com.avogine.render.data.material;

import static org.lwjgl.opengl.GL13.*;

import com.avogine.render.data.texture.Texture;

/**
 * @param diffuse 
 *
 */
public record TexturedMaterial(Texture diffuse) implements Material {

	@Override
	public void bind() {
		if (diffuse != null) {
			// TODO Experiment where if the active texture will get automatically set back to 0 or not
			glActiveTexture(GL_TEXTURE0);
			diffuse.bind();
		}
	}

}
