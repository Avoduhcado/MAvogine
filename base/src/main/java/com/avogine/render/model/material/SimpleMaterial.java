package com.avogine.render.model.material;

import org.joml.Vector4f;

import com.avogine.render.model.material.data.BlinnPhongData;

/**
 * A {@link Material} implementation for Blinn-Phong lighting.
 */
public final class SimpleMaterial implements Material {
	/**
	 * Default color vector to use when no actual color is specified.
	 */
	public static final Vector4f DEFAULT_COLOR = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
	
	private String diffuseMapPath;
	private String specularMapPath;
	private float specularFactor;
	
	/**
	 * @param diffuseMapPath
	 * @param specularMapPath
	 * @param specularFactor
	 */
	public SimpleMaterial(String diffuseMapPath, String specularMapPath, float specularFactor) {
		this.diffuseMapPath = diffuseMapPath;
		this.specularMapPath = specularMapPath;
		this.specularFactor = specularFactor;
	}
	
	/**
	 * @param data
	 */
	public SimpleMaterial(BlinnPhongData data) {
		this(data.diffuseMapPath(), data.specularMapPath(), data.specularFactor());
	}
	
	/**
	 * 
	 */
	public SimpleMaterial() {
		this(new BlinnPhongData());
	}
	
	/**
	 * @return the diffuseTexturePath
	 */
	public String getDiffuseTexturePath() {
		return diffuseMapPath;
	}

	/**
	 * @param diffuseTexturePath the diffuseTexturePath to set
	 */
	public void setDiffuseTexturePath(String diffuseTexturePath) {
		this.diffuseMapPath = diffuseTexturePath;
	}

	/**
	 * @return the specularMapPath
	 */
	public String getSpecularMapPath() {
		return specularMapPath;
	}

	/**
	 * @param specularMapPath the specularMapPath to set
	 */
	public void setSpecularMapPath(String specularMapPath) {
		this.specularMapPath = specularMapPath;
	}

	/**
	 * @return the specularFactor
	 */
	public float getSpecularFactor() {
		return specularFactor;
	}

	/**
	 * @param specularFactor the shininess to set
	 */
	public void setSpecularFactor(float specularFactor) {
		this.specularFactor = specularFactor;
	}
	
}
