package com.avogine.render.opengl.model.mesh.data;

import com.avogine.render.model.mesh.VertexArrayData;
import com.avogine.render.model.mesh.data.InstancedBuffers;

/**
 * @param meshData 
 * @param instancedBuffers 
 * @param maxInstances 
 */
public record InstancedMeshData(MeshData meshData, InstancedBuffers instancedBuffers, int maxInstances) implements VertexArrayData {

	@Override
	public int getVertexCount() {
		return meshData.getVertexCount();
	}

}
