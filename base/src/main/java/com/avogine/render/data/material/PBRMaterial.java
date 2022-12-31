package com.avogine.render.data.material;

import org.joml.Vector3f;

/**
 * @param ambientColor 
 * @param diffuseColor 
 * @param specularColor 
 * @param shininess 
 * @param reflectance 
 *
 */
public record PBRMaterial(Vector3f ambientColor, Vector3f diffuseColor, Vector3f specularColor, float shininess, float reflectance) implements Material {

	@Override
	public void bind() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unbind() {
		// TODO Auto-generated method stub
		
	}

}
