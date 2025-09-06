package com.avogine.render.opengl.model.material;

import java.util.ArrayList;

/**
 *
 */
public final class PBRMaterial extends Material {

	private String albedo;
	private String normalMap;
	private String metallic;
	private String roughness;
	private String ambientOcclusion;
	
	/**
	 * @param albedo
	 * @param normalMap
	 * @param metallic
	 * @param roughness
	 * @param ambientOcclusion
	 */
	public PBRMaterial(String albedo, String normalMap, String metallic, String roughness, String ambientOcclusion) {
		super(new ArrayList<>());
		this.setAlbedo(albedo);
		this.setNormalMap(normalMap);
		this.setMetallic(metallic);
		this.setRoughness(roughness);
		this.setAmbientOcclusion(ambientOcclusion);
	}
	
	/**
	 * Construct a default, empty material.
	 */
	public PBRMaterial() {
		this(null, null, null, null, null);
	}

	/**
	 * @return the albedo
	 */
	public String getAlbedo() {
		return albedo;
	}

	/**
	 * @param albedo the albedo to set
	 */
	public void setAlbedo(String albedo) {
		this.albedo = albedo;
	}

	/**
	 * @return the normalMap
	 */
	public String getNormalMap() {
		return normalMap;
	}

	/**
	 * @param normalMap the normalMap to set
	 */
	public void setNormalMap(String normalMap) {
		this.normalMap = normalMap;
	}

	/**
	 * @return the metallic
	 */
	public String getMetallic() {
		return metallic;
	}

	/**
	 * @param metallic the metallic to set
	 */
	public void setMetallic(String metallic) {
		this.metallic = metallic;
	}

	/**
	 * @return the roughness
	 */
	public String getRoughness() {
		return roughness;
	}

	/**
	 * @param roughness the roughness to set
	 */
	public void setRoughness(String roughness) {
		this.roughness = roughness;
	}

	/**
	 * @return the ambientOcclusion
	 */
	public String getAmbientOcclusion() {
		return ambientOcclusion;
	}

	/**
	 * @param ambientOcclusion the ambientOcclusion to set
	 */
	public void setAmbientOcclusion(String ambientOcclusion) {
		this.ambientOcclusion = ambientOcclusion;
	}
	
}
