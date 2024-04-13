package com.avogine.entity;

import org.joml.*;

/**
 * 
 * @param translation 
 * @param rotation 
 * @param scale 
 * @param modelMatrix 
 */
public record Transform(Vector3f translation, Quaternionf rotation, Vector3f scale, Matrix4f modelMatrix) {

	/**
	 * 
	 */
	public Transform() {
		this(new Vector3f(), new Quaternionf(), new Vector3f(1.0f), new Matrix4f());
	}

	/**
	 * 
	 */
	public void updateModelMatrix() {
		modelMatrix.identity().translationRotateScale(translation, rotation, scale);
	}
	
}
