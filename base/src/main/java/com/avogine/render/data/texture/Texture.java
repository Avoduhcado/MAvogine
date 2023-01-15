package com.avogine.render.data.texture;

/**
 *
 */
public interface Texture {

	/**
	 * 
	 */
	public void bind();
	
	/**
	 * Free the texture memory.
	 */
	public void cleanup();
	
}
