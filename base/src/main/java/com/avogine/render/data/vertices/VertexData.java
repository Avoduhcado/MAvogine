package com.avogine.render.data.vertices;

/**
 * Base interface for wrapper around vertex data.
 */
public interface VertexData extends AutoCloseable {
	
	@Override
	public void close();
	
}
