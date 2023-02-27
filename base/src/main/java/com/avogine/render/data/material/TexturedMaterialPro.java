package com.avogine.render.data.material;

import com.avogine.render.data.texture.Texture;

/**
 * @param diffuse 
 * @param uvScaleX 
 * @param uvScaleY 
 *
 */
public record TexturedMaterialPro(Texture diffuse, float uvScaleX, float uvScaleY) implements Material {

	@Override
	public void bind() {
		if (diffuse != null) {
			diffuse.bind();
		}
	}

}
