package com.avogine.render.opengl.shader;

import static com.avogine.util.resource.ResourceConstants.SHADERS;

import com.avogine.render.opengl.shader.uniform.*;

/**
 *
 */
public class BasicShader extends ShaderProgram {

	public final UniformMat4 projection = new UniformMat4();
	public final UniformMat4 view = new UniformMat4();
	public final UniformMat4 model = new UniformMat4();
	
	public final UniformSampler sampleTexture = new UniformSampler();
	
	/**
	 * 
	 */
	public BasicShader() {
		super(SHADERS.with("basicVertex.glsl"), SHADERS.with("basicFragment.glsl"));
		storeAllUniformLocations(projection, view, model, sampleTexture);
		linkTextureUnits();
	}
	
	private void linkTextureUnits() {
		bind();
		sampleTexture.loadTexUnit(0);
		unbind();
	}

}
