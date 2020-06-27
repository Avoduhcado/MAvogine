package com.avogine.render.shader;

import com.avogine.render.shader.uniform.UniformInteger;
import com.avogine.render.shader.uniform.UniformMat4;
import com.avogine.render.shader.uniform.UniformSampler;
import com.avogine.render.shader.uniform.UniformVec3;

/**
 * This is a basic {@link ShaderProgram} implementation for rendering 3D objects.
 * <p>
 * It can handle displaying a basic 3D scene through the {@link #projectionMatrix}, {@link #viewMatrix}, 
 * and {@link #modelMatrix}.
 * <p>
 * As long as the objects being rendered are supplied with normals, basic lighting can be applied through
 * {@link #lightPosition} and {@link #lightColor}.
 * <p>
 * Textures are optional by setting {@link #useColor} to 1 and supplying a value to {@link #texture}. If
 * {@link #useColor} is 0, a default color will be applied to each vertex.
 */
public class SimpleShader extends ShaderProgram {
	
	public UniformMat4 projectionMatrix = new UniformMat4("projectionMatrix");
	public UniformMat4 viewMatrix = new UniformMat4("viewMatrix");
	public UniformMat4 modelMatrix = new UniformMat4("modelMatrix");

	public UniformVec3 lightPosition = new UniformVec3("lightPosition");
	public UniformVec3 lightColor = new UniformVec3("lightColor");
	
	public UniformSampler texture = new UniformSampler("colorTexture");
	public UniformInteger useColor = new UniformInteger("useColor");
	
	/**
	 * @param vertexShaderFile
	 * @param fragmentShaderFile
	 */
	public SimpleShader(String vertexShaderFile, String fragmentShaderFile) {
		super(vertexShaderFile, fragmentShaderFile);
		storeAllUniformLocations(projectionMatrix, viewMatrix, modelMatrix, lightPosition, lightColor, texture, useColor);
		loadTexUnit();
	}

	private void loadTexUnit() {
		bind();
		texture.loadTexUnit(0);
		unbind();
	}

}
