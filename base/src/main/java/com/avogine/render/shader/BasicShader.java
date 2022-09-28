package com.avogine.render.shader;

import com.avogine.render.shader.uniform.*;

/**
 *
 */
public class BasicShader extends ShaderProgram {

	public final UniformMat4 projection = new UniformMat4();
	public final UniformMat4 view = new UniformMat4();
	public final UniformMat4 model = new UniformMat4();
	
	public final UniformSampler sampleTexture = new UniformSampler();
	
	
	/**
	 * @param vertexShaderFile
	 * @param fragmentShaderFile
	 */
	public BasicShader(String vertexShaderFile, String fragmentShaderFile) {
		super(vertexShaderFile, fragmentShaderFile);
		storeAllUniformLocations(projection, view, model, sampleTexture);
		linkTextureUnits();
	}
	
	private void linkTextureUnits() {
		bind();
		sampleTexture.loadTexUnit(0);
		unbind();
	}

}
