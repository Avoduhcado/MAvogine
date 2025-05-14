package com.avogine.render.data.simple;

import org.joml.Vector3f;

import com.avogine.render.data.mesh.StaticMesh;
import com.avogine.render.data.vertices.array.SimpleVertexArray;

/**
 *
 */
public class SimpleMesh extends StaticMesh<SimpleVertexArray> {
	/**
	 * @param vertexData
	 * @param aabbMin 
	 * @param aabbMax 
	 */
	public SimpleMesh(SimpleVertexArray vertexData, Vector3f aabbMin, Vector3f aabbMax) {
		super(vertexData, aabbMin, aabbMax);
	}

	/**
	 * @param vertexData 
	 */
	public SimpleMesh(SimpleVertexArray vertexData) {
		this(vertexData, new Vector3f(), new Vector3f());
	}
}
