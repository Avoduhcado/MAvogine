package com.avogine.render.model;

import java.util.*;

import org.joml.Vector4f;

import com.avogine.render.opengl.model.mesh.data.MeshData;

/**
 * @param diffuseColor 
 * @param ambientColor 
 * @param specularColor 
 * @param reflectance 
 * @param diffuseTexturePath 
 * @param ambientTexturePath 
 * @param specularTexturePath 
 * @param normalsTexturePath 
 * @param meshes 
 *
 */
public record MaterialData(
		Vector4f diffuseColor,
		Vector4f ambientColor,
		Vector4f specularColor,
		float reflectance,
		String diffuseTexturePath,
		String ambientTexturePath,
		String specularTexturePath,
		String normalsTexturePath,
		List<MeshData> meshes) {
	
	/**
	 * Default color vector to use when no actual color is specified.
	 */
	public static final Vector4f DEFAULT_COLOR = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);

	/**
	 * @param diffuseColor
	 * @param ambientColor
	 * @param specularColor
	 * @param reflectance
	 * @param diffuseTexturePath
	 * @param ambientTexturePath
	 * @param specularTexturePath
	 * @param normalsTexturePath
	 */
	public MaterialData(
			Vector4f diffuseColor,
			Vector4f ambientColor,
			Vector4f specularColor,
			float reflectance,
			String diffuseTexturePath,
			String ambientTexturePath,
			String specularTexturePath,
			String normalsTexturePath) {
		this(diffuseColor, ambientColor, specularColor, reflectance, diffuseTexturePath, ambientTexturePath, specularTexturePath, normalsTexturePath, new ArrayList<>());
	}
	
	/**
	 * 
	 */
	public MaterialData() {
		this(MaterialData.DEFAULT_COLOR, MaterialData.DEFAULT_COLOR, MaterialData.DEFAULT_COLOR, 0.0f, null, null, null, null);
	}

}
