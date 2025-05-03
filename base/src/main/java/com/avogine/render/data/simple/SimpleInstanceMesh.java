package com.avogine.render.data.simple;

import org.joml.Vector3f;

import com.avogine.render.data.mesh.StaticInstanceMesh;
import com.avogine.render.data.vertices.array.SimpleInstanceVertexArray;

/**
 *
 */
public class SimpleInstanceMesh extends StaticInstanceMesh<SimpleInstanceVertexArray> {
	
	/**
	 * @param vertexData
	 * @param materialIndex
	 * @param aabbMax
	 * @param aabbMin
	 * @param maxInstances
	 */
	public SimpleInstanceMesh(SimpleInstanceVertexArray vertexData, int materialIndex, Vector3f aabbMax, Vector3f aabbMin, int maxInstances) {
		super(vertexData, materialIndex, aabbMax, aabbMin, maxInstances);
	}

}
