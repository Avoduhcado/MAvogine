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
	 * @param materialIndex 
	 * @param aabbMin 
	 * @param aabbMax 
	 */
	public SimpleMesh(SimpleVertexArray vertexData, int materialIndex, Vector3f aabbMin, Vector3f aabbMax) {
		super(vertexData, materialIndex, aabbMin, aabbMax);
	}

	/**
	 * @param vertexData 
	 * @param materialIndex 
	 */
	public SimpleMesh(SimpleVertexArray vertexData, int materialIndex) {
		this(vertexData, materialIndex, new Vector3f(), new Vector3f());
	}
}
