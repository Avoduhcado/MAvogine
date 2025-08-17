package com.avogine.render.opengl.model.material.data;

import org.joml.Vector4f;

import com.avogine.render.opengl.model.util.ModelLoader;

/**
 * @param diffuseColor 
 * @param ambientColor 
 * @param specularColor 
 * @param shininess 
 * @param diffuseTexturePath 
 * @param ambientTexturePath 
 * @param specularTexturePath 
 */
public record BlinnPhongData(
		Vector4f diffuseColor,
		Vector4f ambientColor,
		Vector4f specularColor,
		float shininess,
		String diffuseTexturePath,
		String ambientTexturePath,
		String specularTexturePath) {

	/**
	 * @param diffuseColor
	 */
	public BlinnPhongData(Vector4f diffuseColor) {
		this(diffuseColor, ModelLoader.DEFAULT_COLOR, ModelLoader.DEFAULT_COLOR, 0f, null, null, null);
	}
	
	/**
	 * @param diffuseTexturePath
	 */
	public BlinnPhongData(String diffuseTexturePath) {
		this(ModelLoader.DEFAULT_COLOR, ModelLoader.DEFAULT_COLOR, ModelLoader.DEFAULT_COLOR, 0f, diffuseTexturePath, null, null);
	}
	
	/**
	 * 
	 */
	public BlinnPhongData() {
		this(ModelLoader.DEFAULT_COLOR, ModelLoader.DEFAULT_COLOR, ModelLoader.DEFAULT_COLOR, 0f, null, null, null);
	}
	
}
