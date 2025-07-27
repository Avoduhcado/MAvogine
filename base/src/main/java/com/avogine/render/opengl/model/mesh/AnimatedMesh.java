package com.avogine.render.opengl.model.mesh;

import com.avogine.render.model.mesh.data.VertexBuffers;
import com.avogine.render.opengl.*;
import com.avogine.render.opengl.VAO.VBOBuilder;
import com.avogine.render.opengl.model.mesh.data.MeshData;

/**
 * 
 */
public class AnimatedMesh extends Mesh {

	/**
	 * @param meshData 
	 */
	public AnimatedMesh(MeshData meshData) {
		super(meshData.aabb(), meshData.getVertexCount(), initVAO(meshData));
	}
	
	private static VAO initVAO(MeshData meshData) {
		try (VertexBuffers vertexBuffers = meshData.vertexBuffers()) {
			if (vertexBuffers instanceof VertexBuffers(var positions, var normals, var tangents, var bitangents, var textureCoordinates, var c, var weights, var boneIds, var indices)) {
				var vertexAttrib3f = VertexAttrib.Pointer.tightlyPackedUnnormalizedFloat(3);
				var vertexAttrib4f = VertexAttrib.Pointer.tightlyPackedUnnormalizedFloat(4);

				return VAO.gen(
						new VBOBuilder<>(positions, 0, vertexAttrib3f), 
						new VBOBuilder<>(normals, 1, vertexAttrib3f),
						new VBOBuilder<>(tangents, 2, vertexAttrib3f),
						new VBOBuilder<>(bitangents, 3, vertexAttrib3f),
						new VBOBuilder<>(textureCoordinates, 4, VertexAttrib.Pointer.tightlyPackedUnnormalizedFloat(2)),
						new VBOBuilder<>(weights, 5, vertexAttrib4f),
						new VBOBuilder<>(boneIds, 6, vertexAttrib4f),
						() -> VBO.genEBO().bind().bufferData(indices));
			} else {
				throw new IllegalArgumentException("Record deconstruction failed.");
			}
		} finally {
			VAO.unbind();
		}
	}
	
}
