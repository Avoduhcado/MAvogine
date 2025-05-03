package com.avogine.render.data.mesh.parameters;

import com.avogine.render.data.Material;

/**
 *
 */
public interface Materialable {

	/**
	 * @return the index of a {@link Material} in a list of {@link Material}s to use when rendering.
	 */
	public int getMaterialIndex();
	
}
