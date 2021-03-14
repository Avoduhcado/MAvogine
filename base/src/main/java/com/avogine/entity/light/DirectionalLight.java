package com.avogine.entity.light;

import org.joml.*;

/**
 *
 */
public class DirectionalLight extends Light {

	private Vector3f direction;
	
	/**
	 * @param direction
	 * @param ambient
	 * @param diffuse
	 * @param specular
	 */
	public DirectionalLight(Vector3f direction, Vector3f ambient, Vector3f diffuse, Vector3f specular) {
		super(ambient, diffuse, specular);
		this.direction = direction;
	}
	
	/**
	 * @return the direction
	 */
	public Vector3f getDirection() {
		return direction;
	}
	
	/**
	 * @param direction the direction to set
	 */
	public void setDirection(Vector3f direction) {
		this.direction = direction;
	}
	
}
