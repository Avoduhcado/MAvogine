package com.avogine.render.opengl.shader;

import static com.avogine.util.resource.ResourceConstants.SHADERS;

import com.avogine.render.opengl.shader.uniform.*;

/**
 * This is a basic {@link ShaderProgram} implementation for rendering 3D objects with Blinn-Phong lighting.
 * <p>
 * It can handle displaying a basic 3D scene through the perspective {@link #projection}, {@link #view}, and {@link #model} matrices.
 * <p>
 * As long as the objects being rendered are supplied with normals, lighting can be applied through
 * {@link #lightPosition} and {@link #lightColor}.
 */
public class SimpleShader extends ShaderProgram {
	
	// Vertex uniforms
	public final UniformMat4 projection = new UniformMat4();
	public final UniformMat4 view = new UniformMat4();
	public final UniformMat4 model = new UniformMat4();
	public final UniformMat4 normalMatrix = new UniformMat4();
	
	// Fragment uniforms
	public final UniformVec3 lightPosition = new UniformVec3();
	public final UniformVec3 lightColor = new UniformVec3();
	
	public final UniformSampler diffuseMap = new UniformSampler();
	public final UniformSampler specularMap = new UniformSampler();
	public final UniformFloat specularFactor = new UniformFloat();
	
	/**
	 * 
	 */
	public SimpleShader() {
		super(SHADERS.with("simpleVertex.glsl"), SHADERS.with("simpleFragment.glsl"));
		storeAllUniformLocations(
				projection, view, model, normalMatrix, 
				lightPosition, lightColor,
				diffuseMap, specularMap, specularFactor);
		linkTextureUnits();
	}
	
	private void linkTextureUnits() {
		bind();
		diffuseMap.loadTexUnit(0);
		specularMap.loadTexUnit(1);
		unbind();
	}

}
