package com.avogine.render.data.material;

import org.joml.Vector3f;

/**
 *
 */
public interface Material {

	/**
	 * 
	 */
	public static final Vector3f DEFAULT_COLOR = new Vector3f(1f);
	
	/**
	 * 
	 */
	public void bind();
	
	/**
	 * TODO Is this necessary?
	 */
	public void unbind();
	
}
