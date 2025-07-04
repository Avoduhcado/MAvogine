package com.avogine.render.shader;

import static com.avogine.util.resource.ResourceConstants.SHADERS;

import com.avogine.render.shader.uniform.*;

/**
 *
 */
public class AnimatedShader extends ShaderProgram {

	public final UniformMat4 projectionMatrix = new UniformMat4();
	public final UniformMat4 viewMatrix = new UniformMat4();
	public final UniformMat4 modelMatrix = new UniformMat4();
	
	public final UniformMat4 normalMatrix = new UniformMat4();
	public final UniformMat4Array boneMatrices = new UniformMat4Array();
	public final UniformVec3 viewPosition = new UniformVec3();

	public final UniformVec3 lightPosition = new UniformVec3();
	public final UniformVec3 lightColor = new UniformVec3();
	
	public final UniformBoolean hasTexture = new UniformBoolean();
	public final UniformVec3 objectColor = new UniformVec3();
	public final UniformSampler objectTexture = new UniformSampler();
	
	/**
	 * 
	 */
	public AnimatedShader() {
		super(SHADERS.with("animatedVertex.glsl"), SHADERS.with("simpleFragment.glsl"));
		storeAllUniformLocations(projectionMatrix, viewMatrix, modelMatrix, normalMatrix, boneMatrices, viewPosition, lightPosition, lightColor, hasTexture, objectColor, objectTexture);
		linkTextureUnits();
	}
	
	private void linkTextureUnits() {
		bind();
		objectTexture.loadTexUnit(0);
		unbind();
	}
}
