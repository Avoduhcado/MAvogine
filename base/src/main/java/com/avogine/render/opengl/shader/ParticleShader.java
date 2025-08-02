package com.avogine.render.opengl.shader;

import static com.avogine.util.resource.ResourceConstants.SHADERS;

import com.avogine.render.opengl.shader.uniform.*;

/**
 *
 */
public class ParticleShader extends ShaderProgram {

	public final UniformMat4 projection = new UniformMat4();
	public final UniformMat4 view = new UniformMat4();
	
	public final UniformVec3 cameraRightWorldspace = new UniformVec3();
	public final UniformVec3 cameraUpWorldspace = new UniformVec3();
	
	public final UniformSampler particleSampler = new UniformSampler();
	
	/**
	 * 
	 */
	public ParticleShader() {
		super(SHADERS.with("particleVertex.glsl"), SHADERS.with("particleFragment.glsl"));
		storeAllUniformLocations(projection, view, cameraRightWorldspace, cameraUpWorldspace, particleSampler);
		loadTexUnits();
	}
	
	private void loadTexUnits() {
		bind();
		particleSampler.loadTexUnit(0);
		unbind();
	}
	
}
