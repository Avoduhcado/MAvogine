package com.avogine.render.opengl.model.material.data;

/**
 * @param diffuseMapPath 
 * @param specularMapPath 
 * @param specularFactor 
 */
public record BlinnPhongData(String diffuseMapPath, String specularMapPath, float specularFactor) {
	
	/**
	 * @param diffuseTexturePath
	 */
	public BlinnPhongData(String diffuseTexturePath) {
		this(diffuseTexturePath, null, 0.0f);
	}
	
	/**
	 * 
	 */
	public BlinnPhongData() {
		this(null, null, 0.0f);
	}
	
}
