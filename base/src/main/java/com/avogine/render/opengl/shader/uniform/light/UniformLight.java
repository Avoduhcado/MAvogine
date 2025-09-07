package com.avogine.render.opengl.shader.uniform.light;

import com.avogine.entity.light.*;
import com.avogine.render.opengl.shader.uniform.*;

/**
 * @param <T> An instance of {@link Light} that this uniform encapsulates
 *
 */
public abstract class UniformLight<T extends Light> extends Uniform {

	protected final UniformVec3 ambient = new UniformVec3();
	protected final UniformVec3 diffuse = new UniformVec3();
	protected final UniformVec3 specular = new UniformVec3();
	
	@Override
	public void storeUniformLocation(int programID, String name) {
		ambient.storeUniformLocation(programID, name + ".ambient");
		diffuse.storeUniformLocation(programID, name + ".diffuse");
		specular.storeUniformLocation(programID, name + ".specular");
	}
	
	/**
	 * Load the specific {@link Light} subclass parameters to the shader.
	 * @param light
	 */
	public abstract void loadLight(T light);
	
}
