package com.avogine.render.opengl.shader;

import static com.avogine.util.resource.ResourceConstants.SHADERS;

import com.avogine.render.opengl.shader.uniform.*;

/**
 *
 */
public class AnimatedShader extends ShaderProgram {

	public final UniformMat4 projection = new UniformMat4();
	public final UniformMat4 view = new UniformMat4();
	public final UniformMat4 model = new UniformMat4();
	
	public final UniformMat4 normalMatrix = new UniformMat4();
	public final UniformMat4Array boneMatrices = new UniformMat4Array();

	public final UniformVec3 lightPosition = new UniformVec3();
	public final UniformVec3 lightColor = new UniformVec3();

	public final UniformSampler diffuseMap = new UniformSampler();
	public final UniformSampler specularMap = new UniformSampler();
	public final UniformFloat specularFactor = new UniformFloat();
	
	/**
	 * 
	 */
	public AnimatedShader() {
		super(SHADERS.with("animatedVertex.glsl"), SHADERS.with("simpleFragment.glsl"));
		storeAllUniformLocations(projection, view, model,
				normalMatrix, boneMatrices, 
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
