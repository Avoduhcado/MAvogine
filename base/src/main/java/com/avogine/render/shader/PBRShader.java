package com.avogine.render.shader;

import com.avogine.render.shader.uniform.*;

/**
 *
 */
public class PBRShader extends ShaderProgram {

	public final UniformMat4 projectionMatrix = new UniformMat4();
	public final UniformMat4 viewMatrix = new UniformMat4();
	public final UniformMat4 modelMatrix = new UniformMat4();
	
	public final UniformMat3 normalMatrix = new UniformMat3();
	public final UniformMat3 uvMatrix = new UniformMat3();

	public final UniformVec3 lightPosition = new UniformVec3();
	public final UniformVec3 lightColor = new UniformVec3();
	
	public final UniformSampler diffuseTexture = new UniformSampler();
	public final UniformInteger hasNormalMap = new UniformInteger();
	public final UniformSampler normalMap = new UniformSampler();
	
	public PBRShader(String vertexShaderFile, String fragmentShaderFile) {
		super(vertexShaderFile, fragmentShaderFile);
		storeAllUniformLocations(projectionMatrix, viewMatrix, modelMatrix, normalMatrix, uvMatrix, lightPosition, lightColor, diffuseTexture, hasNormalMap, normalMap);
		loadTexUnits();
	}
	
	private void loadTexUnits() {
		bind();
		diffuseTexture.loadTexUnit(0);
		normalMap.loadTexUnit(2);
		unbind();
	}
	
}
