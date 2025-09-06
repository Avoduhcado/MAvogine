package com.avogine.render.opengl.model.material;

import java.util.*;

import com.avogine.render.opengl.model.material.data.BlinnPhongData;
import com.avogine.render.opengl.model.mesh.Mesh;

/**
 * A {@link Material} implementation for Blinn-Phong lighting.
 */
public final class SimpleMaterial extends Material {
	
	private String diffuseMapPath;
	private String specularMapPath;
	private float specularFactor;
	
	/**
	 * @param diffuseMapPath
	 * @param specularMapPath
	 * @param specularFactor
	 * @param meshes
	 */
	public SimpleMaterial(String diffuseMapPath, String specularMapPath, float specularFactor, List<Mesh> meshes) {
		super(meshes);
		this.diffuseMapPath = diffuseMapPath;
		this.specularMapPath = specularMapPath;
		this.specularFactor = specularFactor;
	}
	
	/**
	 * @param diffuseMapPath
	 * @param specularMapPath
	 * @param specularFactor
	 */
	public SimpleMaterial(String diffuseMapPath, String specularMapPath, float specularFactor) {
		this(diffuseMapPath, specularMapPath, specularFactor, new ArrayList<>());
	}
	
	/**
	 * @param data
	 * @param meshes
	 */
	public SimpleMaterial(BlinnPhongData data, List<Mesh> meshes) {
		this(data.diffuseMapPath(), data.specularMapPath(), data.specularFactor(), meshes);
	}
	
	/**
	 * @param data
	 */
	public SimpleMaterial(BlinnPhongData data) {
		this(data, new ArrayList<>());
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
