package com.avogine.render.shader;

import com.avogine.render.shader.uniform.*;

/**
 *
 */
public class NuklearShader extends ShaderProgram {

	public UniformMat4 projectionMatrix = new UniformMat4();
	public UniformSampler nuklearTexture = new UniformSampler();
	
	/**
	 * @param vertexShaderFile
	 * @param fragmentShaderFile
	 */
	public NuklearShader(String vertexShaderFile, String fragmentShaderFile) {
		super(vertexShaderFile, fragmentShaderFile);
		storeAllUniformLocations(projectionMatrix, nuklearTexture);
		loadTexUnit();
	}
	
	private void loadTexUnit() {
		bind();
		nuklearTexture.loadTexUnit(0);
		unbind();
	}

}
