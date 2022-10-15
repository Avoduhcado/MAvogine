package com.avogine.render.shader;

import com.avogine.render.shader.uniform.*;

/**
 *
 */
public class BasicShader extends ShaderProgram {

	public final UniformMat4 projection = new UniformMat4();
	public final UniformMat4 view = new UniformMat4();
	public final UniformMat4 model = new UniformMat4();
	
	public final UniformBoolean isTextured = new UniformBoolean();
	public final UniformSampler sampleTexture = new UniformSampler();
	public final UniformVec3 color = new UniformVec3();
	
	/**
	 * @param vertexShaderFile
	 * @param fragmentShaderFile
	 */
	public BasicShader(String vertexShaderFile, String fragmentShaderFile) {
		super(vertexShaderFile, fragmentShaderFile);
		storeAllUniformLocations(projection, view, model, isTextured, sampleTexture, color);
		linkTextureUnits();
	}
	
	private void linkTextureUnits() {
		bind();
		sampleTexture.loadTexUnit(0);
		unbind();
	}

}
