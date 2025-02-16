package com.avogine.render.shader;

import static com.avogine.util.resource.ResourceConstants.SHADERS;

import com.avogine.render.shader.uniform.*;

/**
 *
 */
public class FontShader extends ShaderProgram {

	public final UniformMat4 projection = new UniformMat4();
	public final UniformMat4 model = new UniformMat4();
	
	public final UniformSampler fontTexture = new UniformSampler();
	public final UniformVec4 textColor = new UniformVec4();
	
	/**
	 * 
	 */
	public FontShader() {
		super(SHADERS.with("textVertex.glsl"), SHADERS.with("textFragment.glsl"));
		storeAllUniformLocations(projection, model, fontTexture, textColor);
		loadTexUnit();
	}
	
	private void loadTexUnit() {
		bind();
		fontTexture.loadTexUnit(0);
		unbind();
	}

}
