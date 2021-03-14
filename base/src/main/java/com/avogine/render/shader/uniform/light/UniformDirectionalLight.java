package com.avogine.render.shader.uniform.light;

import com.avogine.entity.light.*;
import com.avogine.render.shader.uniform.*;

/**
 *
 */
public class UniformDirectionalLight extends UniformLight<DirectionalLight> {

	private final UniformVec3 direction = new UniformVec3();
	
	@Override
	public void storeUniformLocation(int programID, String name) {
		super.storeUniformLocation(programID, name);
		direction.storeUniformLocation(programID, name + ".direction");
	}
	
	@Override
	public void loadLight(DirectionalLight light) {
		direction.loadVec3(light.getDirection());
		
		ambient.loadVec3(light.getAmbient());
		diffuse.loadVec3(light.getDiffuse());
		specular.loadVec3(light.getSpecular());
	}
	
}
