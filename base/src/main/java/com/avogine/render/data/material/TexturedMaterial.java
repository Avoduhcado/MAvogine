package com.avogine.render.data.material;

import com.avogine.render.data.texture.Texture;

/**
 * @param diffuse 
 *
 */
public record TexturedMaterial(Texture diffuse) implements Material {

	@Override
	public void bind() {
		if (diffuse != null) {
			diffuse.bind();
		}
	}

	@Override
	public void unbind() {
		// TODO Auto-generated method stub
		
	}

}
