package com.avogine.render.model.mesh;

import java.nio.Buffer;

/**
 *
 */
public interface Instanceable {

	/**
	 * @param <T>
	 * @param vboIndex
	 * @param offset
	 * @param data
	 */
	public <T extends Buffer> void updateInstanceBuffer(int vboIndex, long offset, T data);
	
	/**
	 * @param <T>
	 * @param vboIndex
	 * @param data
	 */
	public default <T extends Buffer> void updateInstanceBuffer(int vboIndex, T data) {
		updateInstanceBuffer(vboIndex, 0, data);
	}

	/**
	 * @return the maximum number of instances to support for rendering.
	 */
	public int getMaxInstances();
	
}
