package com.avogine.render.data;

import org.joml.*;

public class Material {

	public static final Vector3f DEFAULT_COLOR = new Vector3f(1f);

	private final Vector3f ambientColor;
	private final Vector3f diffuseColor;
	private final Vector3f specularColor;

	private float shininess;
	private float reflectance;
	
	private Texture diffuse;
	private Texture specular;
	private Texture normalMap;
	private Texture emission;
	
	public Material(Vector3f ambientColor, Vector3f diffuseColor, Vector3f specularColor, float shininess, Texture diffuse, float reflectance) {
		this.ambientColor = ambientColor;
		this.diffuseColor = diffuseColor;
		this.specularColor = specularColor;
		this.shininess = shininess;
		this.diffuse = diffuse;
		this.reflectance = reflectance;
	}
	
	public Material(Texture diffuse, Texture specular, Texture emission, float shininess) {
		this.diffuse = diffuse;
		this.specular = specular;
		this.shininess = shininess;
		this.emission = emission;
		
		this.ambientColor = new Vector3f();
		this.diffuseColor = new Vector3f();
		this.specularColor = new Vector3f();
		this.reflectance = 1.0f;
	}

	public Material(Vector3f ambientColor, Vector3f diffuseColor, Vector3f specularColor, float shininess) {
		this(ambientColor, diffuseColor, specularColor, shininess, null, 1.0f);
	}	
	
	public Material(Vector3f color, Texture texture, float shininess) {
		this(color, color, color, shininess);
	}

	public Material(Texture texture, float shininess) {
		this(DEFAULT_COLOR, texture, shininess);
	}

	public Material(Texture diffuse) {
		this(diffuse, 32f);
	}
	
	public Material() {
		this(null);
	}

	/**
	 * @return the ambient color
	 */
	public Vector3f getAmbientColor() {
		return ambientColor;
	}
	
	/**
	 * @param ambient the ambient color to set
	 */
	public void setAmbientColor(Vector3f ambient) {
		this.ambientColor.set(ambient);
	}
	
	public Vector3f getDiffuseColor() {
		return diffuseColor;
	}
	
	public void setDiffuseColor(Vector3f diffuseColor) {
		this.diffuseColor.set(diffuseColor);
	}
	
	public Vector3f getSpecularColor() {
		return specularColor;
	}
	
	public void setSpecularColor(Vector3f specularColor) {
		this.specularColor.set(specularColor);
	}
	
	public float getShininess() {
		return shininess;
	}
	
	public void setShininess(float shininess) {
		this.shininess = shininess;
	}

	public float getReflectance() {
		return reflectance;
	}
	
	public void setReflectance(float reflectance) {
		this.reflectance = reflectance;
	}
	
	public boolean isTextured() {
		return diffuse != null;
	}
	
	public Texture getDiffuse() {
		return diffuse;
	}
	
	public void setDiffuse(Texture diffuse) {
		this.diffuse = diffuse;
	}
	
	public Texture getSpecular() {
		return specular;
	}
	
	public void setSpecular(Texture specular) {
		this.specular = specular;
	}
	
	/**
	 * @return the emission
	 */
	public Texture getEmission() {
		return emission;
	}
	
	/**
	 * @param emission the emission to set
	 */
	public void setEmission(Texture emission) {
		this.emission = emission;
	}
	
	public boolean hasNormalMap() {
		return normalMap != null;
	}
	
	public Texture getNormalMap() {
		return normalMap;
	}
	
	public void setNormalMap(Texture normalMap) {
		this.normalMap = normalMap;
	}

}
