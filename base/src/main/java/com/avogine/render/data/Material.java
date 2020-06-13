package com.avogine.render.data;

import org.joml.Vector4f;

public class Material {

	public static final Vector4f DEFAULT_COLOR = new Vector4f(1f);

	private Vector4f diffuseColor;
	private Vector4f specularColor;

	private float shininess;
	private float reflectance;
	
	private Texture texture;
	private Texture normalMap;
	
	public Material() {
		diffuseColor = DEFAULT_COLOR;
		specularColor = DEFAULT_COLOR;
		texture = null;
		reflectance = 0;
	}
	
	public Material(Vector4f color, float reflectance) {
		this(color, color, reflectance);
	}
	
	public Material(Vector4f diffuseColor, Vector4f specularColor, float reflectance) {
		this(diffuseColor, specularColor, null, reflectance);
	}	
	
	public Material(Texture texture) {
		this(DEFAULT_COLOR, DEFAULT_COLOR, texture, 0);
	}
	
	public Material(Texture texture, float reflectance) {
		this(DEFAULT_COLOR, DEFAULT_COLOR, texture, reflectance);
	}
	
	public Material(Vector4f diffuseColor, Vector4f specularColor, Texture texture, float reflectance) {
		this.diffuseColor = diffuseColor;
		this.specularColor = specularColor;
		this.texture = texture;
		this.reflectance = reflectance;
	}
	
	public Vector4f getDiffuseColor() {
		return diffuseColor;
	}
	
	public void setDiffuseColor(Vector4f diffuseColor) {
		this.diffuseColor = diffuseColor;
	}
	
	public Vector4f getSpecularColor() {
		return specularColor;
	}
	
	public void setSpecularColor(Vector4f specularColor) {
		this.specularColor = specularColor;
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
		return texture != null;
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public void setTexture(Texture texture) {
		this.texture = texture;
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
