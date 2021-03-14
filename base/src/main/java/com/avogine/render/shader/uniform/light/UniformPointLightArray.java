package com.avogine.render.shader.uniform.light;

import java.util.*;

import com.avogine.entity.light.*;
import com.avogine.render.shader.uniform.*;

/**
 *
 */
public class UniformPointLightArray extends Uniform {

	private UniformPointLight[] pointLightUniforms;
	
	/**
	 * @param size
	 */
	public UniformPointLightArray(int size) {
		pointLightUniforms = new UniformPointLight[size];
		for (int i = 0; i < size; i++) {
			pointLightUniforms[i] = new UniformPointLight();
		}
	}
	
	@Override
	public void storeUniformLocation(int programID, String name) {
		for (int i = 0; i < pointLightUniforms.length; i++) {
			pointLightUniforms[i].storeUniformLocation(programID, name + "[" + i + "]");
		}
	}

	public void loadPointLightArray(PointLight[] lights) {
		for (int i = 0; i < lights.length; i++) {
			pointLightUniforms[i].loadLight(lights[i]);
		}
	}
	
	public void loadPointLight(PointLight value, int index) {
		pointLightUniforms[index].loadLight(value);
	}
	
}
