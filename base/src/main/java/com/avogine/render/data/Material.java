package com.avogine.render.data;

import org.joml.Vector4f;

/**
 *
 */
public class Material {

	/**
	 * 
	 */
	public static final Vector4f DEFAULT_COLOR = new Vector4f(1f);

	private Vector4f diffuseColor;
	private Texture diffuseTexture;
	private Vector4f ambientColor;
	private Texture ambientTexture;
	private Vector4f specularColor;
	private Texture specularTexture;
	
	/**
	 * @param diffuseColor 
	 * @param diffuseTexture 
	 * @param ambientColor 
	 * @param ambientTexture 
	 * @param specularColor 
	 * @param specularTexture 
	 */
	public Material(Vector4f diffuseColor, Texture diffuseTexture, Vector4f ambientColor, Texture ambientTexture, Vector4f specularColor, Texture specularTexture) {
		this.diffuseColor = diffuseColor;
		this.diffuseTexture = diffuseTexture;
		this.ambientColor = ambientColor;
		this.ambientTexture = ambientTexture;
		this.specularColor = specularColor;
		this.specularTexture = specularTexture;
	}
	
	/**
	 * @param diffuse
	 * @param ambient
	 * @param specular
	 */
	public Material(Vector4f diffuse, Vector4f ambient, Vector4f specular) {
		this(diffuse, null, ambient, null, specular, null);
	}
	
	/**
	 * @param diffuse
	 * @param ambient
	 * @param specular
	 */
	public Material(Texture diffuse, Texture ambient, Texture specular) {
		this(DEFAULT_COLOR, diffuse, DEFAULT_COLOR, ambient, DEFAULT_COLOR, specular);
	}
	
	/**
	 * @param diffuse
	 */
	public Material(Texture diffuse) {
		this(diffuse, null, null);
	}
	
	/**
	 * @param diffuse
	 */
	public Material(Vector4f diffuse) {
		this(diffuse, DEFAULT_COLOR, DEFAULT_COLOR);
	}
	
	/**
	 * 
	 */
	public Material() {
		this(DEFAULT_COLOR);
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
	 * @return the diffuseTexture
	 */
	public Texture getDiffuseTexture() {
		return diffuseTexture;
	}
	
	/**
	 * @param diffuseTexture the diffuseTexture to set
	 */
	public void setDiffuseTexture(Texture diffuseTexture) {
		this.diffuseTexture = diffuseTexture;
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
	 * @return the ambientTexture
	 */
	public Texture getAmbientTexture() {
		return ambientTexture;
	}
	
	/**
	 * @param ambientTexture the ambientTexture to set
	 */
	public void setAmbientTexture(Texture ambientTexture) {
		this.ambientTexture = ambientTexture;
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
	 * @return the specularTexture
	 */
	public Texture getSpecularTexture() {
		return specularTexture;
	}
	
	/**
	 * @param specularTexture the specularTexture to set
	 */
	public void setSpecularTexture(Texture specularTexture) {
		this.specularTexture = specularTexture;
	}
	
}
