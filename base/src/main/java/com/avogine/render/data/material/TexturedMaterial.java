package com.avogine.render.data.material;

import com.avogine.render.data.TextureAtlas;

/**
 * @param diffuse 
 *
 */
public record TexturedMaterial(TextureAtlas diffuse) implements Material {

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
