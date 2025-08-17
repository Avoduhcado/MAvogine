package com.avogine.render.opengl.model.mesh;

import com.avogine.render.opengl.VertexArrayObject;

/**
 * Parent type of a general Mesh implementation.
 */
public abstract sealed class Mesh extends VertexArrayObject permits StaticMesh, InstancedMesh, AnimatedMesh {

	protected Mesh(Builder builder, int vertexCount) {
		super(builder, vertexCount);
	}
	
}
