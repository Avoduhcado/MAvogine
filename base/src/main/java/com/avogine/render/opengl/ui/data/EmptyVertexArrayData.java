package com.avogine.render.opengl.ui.data;

import com.avogine.render.model.mesh.VertexArrayData;

/**
 *
 */
public record EmptyVertexArrayData() implements VertexArrayData {

	public static final EmptyVertexArrayData EMPTY = new EmptyVertexArrayData();
	
	@Override
	public int getVertexCount() {
		return -1;
	}

}
