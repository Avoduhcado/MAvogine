package com.avogine.render.model.mesh;

import java.nio.Buffer;

/**
 *
 */
public interface Instanceable {

	/**
	 * @param <T>
	 * @param vboIndex
	 * @param buffer
	 */
	public <T extends Buffer> void updateInstanceBuffer(int vboIndex, T buffer);

	/**
	 * @return the maximum number of instances to support for rendering.
	 */
	public int getMaxInstances();
	
}
