package com.avogine.render.shader;

import com.avogine.render.shader.uniform.UniformMat4;
import com.avogine.render.shader.uniform.UniformSampler;

/**
 *
 */
public class SkyboxShader extends ShaderProgram {

	public UniformMat4 projection = new UniformMat4();
	public UniformMat4 view = new UniformMat4();
	
	public UniformSampler skybox = new UniformSampler();
	
	/**
	 * @param vertexShaderFile
	 * @param fragmentShaderFile
	 */
	public SkyboxShader(String vertexShaderFile, String fragmentShaderFile) {
		super(vertexShaderFile, fragmentShaderFile);
		storeAllUniformLocations(projection, view, skybox);
		loadTexUnit();
	}
	
	private void loadTexUnit() {
		bind();
		skybox.loadTexUnit(0);
		unbind();
	}

}
