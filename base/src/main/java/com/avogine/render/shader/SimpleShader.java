package com.avogine.render.shader;

import com.avogine.render.shader.uniform.*;

/**
 * This is a basic {@link ShaderProgram} implementation for rendering 3D objects with simple lighting and color.
 * <p>
 * It can handle displaying a basic 3D scene through the {@link #projectionMatrix}, {@link #viewMatrix}, 
 * and {@link #modelMatrix}.
 * <p>
 * As long as the objects being rendered are supplied with normals, basic lighting can be applied through
 * {@link #lightPosition} and {@link #lightColor}.
 */
public class SimpleShader extends ShaderProgram {
	
	public final UniformMat4 projectionMatrix = new UniformMat4();
	public final UniformMat4 viewMatrix = new UniformMat4();
	public final UniformMat4 modelMatrix = new UniformMat4();
	
	public final UniformMat4 normalMatrix = new UniformMat4();
	public final UniformVec3 viewPosition = new UniformVec3();

	public final UniformVec3 lightPosition = new UniformVec3();
	public final UniformVec3 lightColor = new UniformVec3();
	
	public final UniformBoolean hasTexture = new UniformBoolean();
	public final UniformVec3 objectColor = new UniformVec3();
	public final UniformSampler objectTexture = new UniformSampler();

	/**
	 * @param vertexShaderFile
	 * @param fragmentShaderFile
	 */
	public SimpleShader(String vertexShaderFile, String fragmentShaderFile) {
		super(vertexShaderFile, fragmentShaderFile);
		storeAllUniformLocations(projectionMatrix, viewMatrix, modelMatrix, normalMatrix, viewPosition, lightPosition, lightColor, hasTexture, objectColor, objectTexture);
		linkTextureUnits();
	}
	
	private void linkTextureUnits() {
		bind();
		objectTexture.loadTexUnit(0);
		unbind();
	}

}
