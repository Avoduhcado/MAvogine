package com.avogine.entity.light;

import org.joml.*;

/**
 *
 */
public class PointLight extends Light {

	private Vector3f position;
	private Vector3f transformPosition;

	// Attenuation
	private float constant;
	private float linear;
	private float quadratic;
	
	/**
	 * @param position 
	 * @param ambient
	 * @param diffuse
	 * @param specular
	 */
	public PointLight(Vector3f position, Vector3f ambient, Vector3f diffuse, Vector3f specular) {
		super(ambient, diffuse, specular);
		this.position = position;
		this.transformPosition = new Vector3f();

		constant = 1.0f;
		linear = 0.007f;
		quadratic = 0.0002f;
	}
	
	/**
	 * @return the position
	 */
	public Vector3f getPosition() {
		return position;
	}
	
	/**
	 * @param position the position to set
	 */
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	
	/**
	 * @return the transformPosition
	 */
	public Vector3f getTransformPosition() {
		return transformPosition;
	}
	
	/**
	 * @param transformPosition the transformPosition to set
	 */
	public void setTransformPosition(Vector3f transformPosition) {
		this.transformPosition = transformPosition;
	}
	
	/**
	 * @return the constant
	 */
	public float getConstant() {
		return constant;
	}
	
	/**
	 * @param constant the constant to set
	 */
	public void setConstant(float constant) {
		this.constant = constant;
	}
	
	/**
	 * @return the linear
	 */
	public float getLinear() {
		return linear;
	}
	
	/**
	 * @param linear the linear to set
	 */
	public void setLinear(float linear) {
		this.linear = linear;
	}
	
	/**
	 * @return the quadratic
	 */
	public float getQuadratic() {
		return quadratic;
	}
	
	/**
	 * @param quadratic the quadratic to set
	 */
	public void setQuadratic(float quadratic) {
		this.quadratic = quadratic;
	}

}
