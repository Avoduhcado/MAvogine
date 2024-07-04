package com.avogine.render.shader;

import com.avogine.render.shader.uniform.*;

/**
 *
 */
public class FontShader extends ShaderProgram {

	public UniformMat4 projection = new UniformMat4();
	public UniformMat4 model = new UniformMat4();
	
	public UniformSampler fontTexture = new UniformSampler();
	public UniformVec4 textColor = new UniformVec4();
	
	/**
	 * @param vertexShaderFile
	 * @param fragmentShaderFile
	 */
	public FontShader(String vertexShaderFile, String fragmentShaderFile) {
		super(vertexShaderFile, fragmentShaderFile);
		storeAllUniformLocations(projection, model, fontTexture, textColor);
		loadTexUnit();
	}
	
	private void loadTexUnit() {
		bind();
		fontTexture.loadTexUnit(0);
		unbind();
	}

}
