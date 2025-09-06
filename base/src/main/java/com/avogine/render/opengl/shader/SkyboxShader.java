package com.avogine.render.opengl.shader;

import com.avogine.render.opengl.shader.uniform.*;

/**
 *
 */
public class SkyboxShader extends ShaderProgram {

	public final UniformMat4 projection = new UniformMat4();
	public final UniformMat4 view = new UniformMat4();
	
	public final UniformSampler skybox = new UniformSampler();
	
	/**
	 * @param vertexShaderFilePath
	 * @param fragmentShaderFilePath
	 */
	public SkyboxShader(String vertexShaderFilePath, String fragmentShaderFilePath) {
		super(vertexShaderFilePath, fragmentShaderFilePath);
		storeAllUniformLocations(projection, view, skybox);
		loadTexUnit();
	}
	
	private void loadTexUnit() {
		bind();
		skybox.loadTexUnit(0);
		unbind();
	}

}
