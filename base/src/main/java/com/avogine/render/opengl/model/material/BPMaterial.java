package com.avogine.render.opengl.model.material;

import java.util.*;

import org.joml.Vector4f;

import com.avogine.render.opengl.model.material.data.BlinnPhongData;
import com.avogine.render.opengl.model.mesh.Mesh;

/**
 * A Blinn-Phong lighting reflection {@link Material} implementation.
 */
public final class BPMaterial extends Material {
	
	private Vector4f diffuseColor;
	private Vector4f ambientColor;
	private Vector4f specularColor;
	
	private float shininess;
	
	private String diffuseTexturePath;
	private String ambientTexturePath;
	private String specularTexturePath;

	/**
	 * @param data
	 * @param meshes
	 */
	public BPMaterial(BlinnPhongData data, List<Mesh> meshes) {
		super(meshes);
		this.diffuseColor = data.diffuseColor();
		this.ambientColor = data.ambientColor();
		this.specularColor = data.specularColor();
		this.shininess = data.shininess();
		this.diffuseTexturePath = data.diffuseTexturePath();
		this.ambientTexturePath = data.ambientTexturePath();
		this.specularTexturePath = data.specularTexturePath();
	}
	
	/**
	 * @param data
	 */
	public BPMaterial(BlinnPhongData data) {
		this(data, new ArrayList<>());
	}
	
	/**
	 * 
	 */
	public BPMaterial() {
		this(new BlinnPhongData());
	}
	
	/**
	 * @return the diffuseColor
	 */
	public Vector4f getDiffuseColor() {
		return diffuseColor;
	}

	/**
	 * @param diffuseColor the diffuseColor to set
	 */
	public void setDiffuseColor(Vector4f diffuseColor) {
		this.diffuseColor = diffuseColor;
	}

	/**
	 * @return the ambientColor
	 */
	public Vector4f getAmbientColor() {
		return ambientColor;
	}

	/**
	 * @param ambientColor the ambientColor to set
	 */
	public void setAmbientColor(Vector4f ambientColor) {
		this.ambientColor = ambientColor;
	}

	/**
	 * @return the specularColor
	 */
	public Vector4f getSpecularColor() {
		return specularColor;
	}

	/**
	 * @param specularColor the specularColor to set
	 */
	public void setSpecularColor(Vector4f specularColor) {
		this.specularColor = specularColor;
	}

	/**
	 * @return the diffuseTexturePath
	 */
	public String getDiffuseTexturePath() {
		return diffuseTexturePath;
	}

	/**
	 * @param diffuseTexturePath the diffuseTexturePath to set
	 */
	public void setDiffuseTexturePath(String diffuseTexturePath) {
		this.diffuseTexturePath = diffuseTexturePath;
	}

	/**
	 * @return the ambientTexturePath
	 */
	public String getAmbientTexturePath() {
		return ambientTexturePath;
	}

	/**
	 * @param ambientTexturePath the ambientTexturePath to set
	 */
	public void setAmbientTexturePath(String ambientTexturePath) {
		this.ambientTexturePath = ambientTexturePath;
	}

	/**
	 * @return the specularTexturePath
	 */
	public String getSpecularTexturePath() {
		return specularTexturePath;
	}

	/**
	 * @param specularTexturePath the specularTexturePath to set
	 */
	public void setSpecularTexturePath(String specularTexturePath) {
		this.specularTexturePath = specularTexturePath;
	}
	
	/**
	 * @return the shininess
	 */
	public float getShininess() {
		return shininess;
	}

	/**
	 * @param shininess the shininess to set
	 */
	public void setShininess(float shininess) {
		this.shininess = shininess;
	}
	
}
