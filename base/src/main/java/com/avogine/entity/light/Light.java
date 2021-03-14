package com.avogine.entity.light;

import org.joml.*;

/**
 *
 */
public abstract class Light {

	private Vector3f ambient;
	private Vector3f diffuse;
	private Vector3f specular;
	
	/**
	 * @param ambient
	 * @param diffuse
	 * @param specular
	 */
	protected Light(Vector3f ambient, Vector3f diffuse, Vector3f specular) {
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular;
	}
	
	/**
	 * @return
	 */
	public Vector3f getAmbient() {
		return ambient;
	}
	
	/**
	 * @param ambient
	 */
	public void setAmbient(Vector3f ambient) {
		this.ambient = ambient;
	}

	/**
	 * @return the diffuse
	 */
	public Vector3f getDiffuse() {
		return diffuse;
	}

	/**
	 * @param diffuse the diffuse to set
	 */
	public void setDiffuse(Vector3f diffuse) {
		this.diffuse = diffuse;
	}

	/**
	 * @return the specular
	 */
	public Vector3f getSpecular() {
		return specular;
	}

	/**
	 * @param specular the specular to set
	 */
	public void setSpecular(Vector3f specular) {
		this.specular = specular;
	}
	
}
