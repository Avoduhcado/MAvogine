package com.avogine.render.data.material;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

import org.joml.*;

import com.avogine.render.data.texture.Texture;

/**
 * @param diffuse 
 * @param diffuseColor 
 * @param specular 
 * @param specularColor 
 * @param ambientColor 
 * @param shininess 
 * @param reflectance 
 * @param normalMap 
 * @param uvTransform 
 *
 */
public record PBRMaterial(Texture diffuse, Vector3f diffuseColor, Texture specular, Vector3f specularColor, Vector3f ambientColor, float shininess, float reflectance, Texture normalMap, Matrix3f uvTransform) implements Material {

	@Override
	public void bind() {
		if (diffuse != null) {
			glActiveTexture(GL_TEXTURE0);
			diffuse.bind();
		}
		if (specular != null) {
			glActiveTexture(GL_TEXTURE1);
			specular.bind();
		} else {
			glActiveTexture(GL_TEXTURE1);
			// TODO Experiment if a leftover texture bound to GL_TEXTURE1 would otherwise get drawn here if we didn't bind the target to 0
			glBindTexture(GL_TEXTURE_2D, 0);
		}
		if (normalMap != null) {
			glActiveTexture(GL_TEXTURE2);
			normalMap.bind();
		}else {
			glActiveTexture(GL_TEXTURE2);
			glBindTexture(GL_TEXTURE_2D, 0);
		}
	}

}
