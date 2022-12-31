package com.avogine.render.data.material;

import com.avogine.render.data.TextureAtlas;

/**
 * @param diffuse 
 * @param specular 
 * @param ambient 
 * @param shininess 
 * @param reflectance 
 *
 */
public record PBRTexturedMaterial(TextureAtlas diffuse, TextureAtlas specular, TextureAtlas ambient, float shininess, float reflectance) implements Material {

	@Override
	public void bind() {
		if (diffuse != null) {
			diffuse.bind();
		}
		if (specular != null) {
			specular.bind();
		}
		if (ambient != null) {
			ambient.bind();
		}
	}

	@Override
	public void unbind() {
		
	}

}
