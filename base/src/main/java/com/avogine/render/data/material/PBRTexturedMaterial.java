package com.avogine.render.data.material;

import com.avogine.render.data.texture.Texture;

/**
 * @param diffuse 
 * @param specular 
 * @param ambient 
 * @param shininess 
 * @param reflectance 
 *
 */
public record PBRTexturedMaterial(Texture diffuse, Texture specular, Texture ambient, float shininess, float reflectance) implements Material {

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

}
