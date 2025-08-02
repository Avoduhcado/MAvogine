package com.avogine.render.opengl.model;

import java.util.*;

import org.joml.Vector4f;

import com.avogine.render.model.MaterialData;
import com.avogine.render.model.mesh.Boundable;
import com.avogine.render.opengl.model.mesh.*;

/**
 */
public class Material {
	
	private Vector4f diffuseColor;
	private Vector4f ambientColor;
	private Vector4f specularColor;
	
	private float reflectance;
	
	private String diffuseTexturePath;
	private String ambientTexturePath;
	private String specularTexturePath;
	
	private String normalsTexturePath;
	
	private List<MaterialMesh<?>> meshes;

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
	 */
	public Material(Vector4f diffuseColor, Vector4f ambientColor, Vector4f specularColor, float reflectance,
			String diffuseTexturePath, String ambientTexturePath, String specularTexturePath, String normalsTexturePath,
			List<MaterialMesh<?>> meshes) {
		this.diffuseColor = diffuseColor;
		this.ambientColor = ambientColor;
		this.specularColor = specularColor;
		this.reflectance = reflectance;
		this.diffuseTexturePath = diffuseTexturePath;
		this.ambientTexturePath = ambientTexturePath;
		this.specularTexturePath = specularTexturePath;
		this.normalsTexturePath = normalsTexturePath;
		this.meshes = meshes;
	}
	
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
	public Material(Vector4f diffuseColor, Vector4f ambientColor, Vector4f specularColor, float reflectance,
			String diffuseTexturePath, String ambientTexturePath, String specularTexturePath, String normalsTexturePath) {
		this.diffuseColor = diffuseColor;
		this.ambientColor = ambientColor;
		this.specularColor = specularColor;
		this.reflectance = reflectance;
		this.diffuseTexturePath = diffuseTexturePath;
		this.ambientTexturePath = ambientTexturePath;
		this.specularTexturePath = specularTexturePath;
		this.normalsTexturePath = normalsTexturePath;
		this.meshes = new ArrayList<>();
	}

	/**
	 * @param diffuseColor
	 */
	public Material(Vector4f diffuseColor) {
		this(diffuseColor, MaterialData.DEFAULT_COLOR, MaterialData.DEFAULT_COLOR, 0f, null, null, null, null);
	}

	/**
	 * @param diffuseTexturePath
	 */
	public Material(String diffuseTexturePath) {
		this(MaterialData.DEFAULT_COLOR, MaterialData.DEFAULT_COLOR, MaterialData.DEFAULT_COLOR, 0f, diffuseTexturePath, null, null, null);
	}
	
	/**
	 * 
	 */
	public Material() {
		this(MaterialData.DEFAULT_COLOR, MaterialData.DEFAULT_COLOR, MaterialData.DEFAULT_COLOR, 0f, null, null, null, null);
	}
	
	/**
	 * Free all {@link MaterialMesh} data.
	 */
	public void cleanup() {
		meshes.forEach(MaterialMesh::cleanup);
		meshes.clear();
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
	 * @return the normalsTexturePath
	 */
	public String getNormalsTexturePath() {
		return normalsTexturePath;
	}
	
	/**
	 * @param normalsTexturePath the normalsTexturePath to set
	 */
	public void setNormalsTexturePath(String normalsTexturePath) {
		this.normalsTexturePath = normalsTexturePath;
	}

	/**
	 * @return the reflectance
	 */
	public float getReflectance() {
		return reflectance;
	}

	/**
	 * @param reflectance the reflectance to set
	 */
	public void setReflectance(float reflectance) {
		this.reflectance = reflectance;
	}
	
	/**
	 * Retrieve all {@link MaterialMesh}es contained in this material.
	 * </br>
	 * This method should only be used for debugging purposes. Intended usage is through
	 * the typed retrieval methods, i.e. {@link Material#getStaticMeshes()}, {@link Material#getAnimatedMeshes()}.
	 * @return the meshes
	 */
	public List<MaterialMesh<?>> getMeshes() {
		return meshes;
	}
	
	/**
	 * @return
	 */
	public List<Mesh2> getStaticMeshes() {
		return meshes.stream()
				.filter(Mesh2.class::isInstance)
				.map(Mesh2.class::cast)
				.toList();
	}
	
	/**
	 * @return
	 */
	public List<AnimatedMesh2> getAnimatedMeshes() {
		return meshes.stream()
				.filter(AnimatedMesh2.class::isInstance)
				.map(AnimatedMesh2.class::cast)
				.toList();
	}
	
	/**
	 * @return
	 */
	public List<InstancedMesh2> getInstancedMeshes() {
		return meshes.stream()
				.filter(InstancedMesh2.class::isInstance)
				.map(InstancedMesh2.class::cast)
				.toList();
	}
	
	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends MaterialMesh<?> & Boundable> List<T> getBoundableMeshes() {
		return meshes.stream()
				.filter(mesh -> mesh instanceof MaterialMesh && mesh instanceof Boundable)
				.map(mesh -> (T) mesh)
				.toList();
	}
	
	/**
	 * @param mesh
	 */
	public void addMesh(MaterialMesh<?> mesh) {
		meshes.add(mesh);
	}
	
	/**
	 * @param meshes the meshes to set
	 */
	public void setMeshes(List<MaterialMesh<?>> meshes) {
		this.meshes = meshes;
	}
	
	/**
	 * @return
	 */
	public boolean isMeshesEmpty() {
		return meshes.isEmpty();
	}
	
}
