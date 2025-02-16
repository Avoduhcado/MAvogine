package com.avogine.entity.light;

import org.joml.Math;
import org.joml.Vector3f;

/**
 *
 */
public class SpotLight extends Light {

	private Vector3f position;
	private Vector3f direction;
	
	private float innerCutOff;
	private float outerCutOff;
	
	// Attenuation
	private float constant;
	private float linear;
	private float quadratic;
	
	/**
	 * @param position 
	 * @param direction 
	 * @param innerCutOff 
	 * @param outerCutOff 
	 * @param ambient
	 * @param diffuse
	 * @param specular
	 */
	public SpotLight(Vector3f position, Vector3f direction, float innerCutOff, float outerCutOff, Vector3f ambient, Vector3f diffuse, Vector3f specular) {
		super(ambient, diffuse, specular);
		this.position = position;
		this.direction = direction;
		this.innerCutOff = innerCutOff;
		this.outerCutOff = outerCutOff;

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

	/**
	 * 
	 * @return The cosine of the innerCutOff value converted to radians
	 */
	public float getInnerCutOffCosineRadians() {
		return Math.cos(Math.toRadians(innerCutOff));
	}
	
	/**
	 * @return the innerCutOff
	 */
	public float getInnerCutOff() {
		return innerCutOff;
	}
	
	/**
	 * @param innerCutOff the innerCutOff to set
	 */
	public void setInnerCutOff(float innerCutOff) {
		this.innerCutOff = innerCutOff;
	}

	/**
	 * 
	 * @return The cosine of the innerCutOff value converted to radians
	 */
	public float getOuterCutOffCosineRadians() {
		return Math.cos(Math.toRadians(outerCutOff));
	}
	
	/**
	 * @return the outerCutOff
	 */
	public float getOuterCutOff() {
		return outerCutOff;
	}
	
	/**
	 * @param outerCutOff the outerCutOff to set
	 */
	public void setOuterCutOff(float outerCutOff) {
		this.outerCutOff = outerCutOff;
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
