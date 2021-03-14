package com.avogine.render.shader.uniform;

import com.avogine.render.data.*;

/**
 *
 */
public class UniformMaterial extends Uniform {

	public final UniformSampler diffuse = new UniformSampler();
	public final UniformSampler specular = new UniformSampler();
	public final UniformFloat shininess = new UniformFloat();
	
	@Override
	public void storeUniformLocation(int programID, String name) {
		diffuse.storeUniformLocation(programID, name + ".diffuse");
		specular.storeUniformLocation(programID, name + ".specular");
		shininess.storeUniformLocation(programID, name + ".shininess");
	}
	
	/**
	 * @param material
	 */
	public void loadMaterial(Material material) {
		// TODO Do we want to handle loading the tex unit here, or handle activating/binding textures here? Or neither
		shininess.loadFloat(material.getShininess());
	}
	
}
