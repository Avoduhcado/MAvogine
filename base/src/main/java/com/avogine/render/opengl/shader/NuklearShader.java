package com.avogine.render.opengl.shader;

import static com.avogine.util.resource.ResourceConstants.SHADERS;

import com.avogine.render.opengl.shader.uniform.*;

/**
 *
 */
public class NuklearShader extends ShaderProgram {

	public final UniformMat4 projectionMatrix = new UniformMat4();
	public final UniformSampler nuklearTexture = new UniformSampler();
	
	/**
	 * 
	 */
	public NuklearShader() {
		super(SHADERS.with("nuklearVertex.glsl"), SHADERS.with("nuklearFragment.glsl"));
		storeAllUniformLocations(projectionMatrix, nuklearTexture);
		loadTexUnit();
	}
	
	private void loadTexUnit() {
		bind();
		nuklearTexture.loadTexUnit(0);
		unbind();
	}

}
