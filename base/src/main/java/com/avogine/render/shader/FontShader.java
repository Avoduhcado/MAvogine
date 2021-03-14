package com.avogine.render.shader;

import com.avogine.render.shader.uniform.UniformMat4;
import com.avogine.render.shader.uniform.UniformSampler;

/**
 *
 */
public class FontShader extends ShaderProgram {

	public UniformMat4 projection = new UniformMat4();
	public UniformMat4 model = new UniformMat4();
	
	public UniformSampler fontSample = new UniformSampler();
	
	/**
	 * @param vertexShaderFile
	 * @param fragmentShaderFile
	 */
	public FontShader(String vertexShaderFile, String fragmentShaderFile) {
		super(vertexShaderFile, fragmentShaderFile);
		storeAllUniformLocations(projection, model, fontSample);
		loadTexUnit();
	}
	
	private void loadTexUnit() {
		bind();
		fontSample.loadTexUnit(0);
		unbind();
	}

}
