package com.avogine.render.opengl.ui.data;

import java.nio.Buffer;

import com.avogine.render.model.mesh.VertexArrayData;

/**
 * @param buffer 
 * @param <T> 
 */
public record BufferVertexArrayData<T extends Buffer>(T buffer) implements VertexArrayData {
	
	@Override
	public int getVertexCount() {
		return -1;
	}

}
