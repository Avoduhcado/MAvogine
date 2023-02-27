package com.avogine.render.data.texture;

/**
 *
 */
public interface Texture {

	/**
	 * Bind the texture to a texture target to be used in rendering.
	 * </p>
	 * This method makes no distinction about what active texture to use and such
	 * details should be left up to the implementor.
	 */
	public void bind();
	
	/**
	 * Free the texture memory.
	 */
	public void cleanup();
	
}
