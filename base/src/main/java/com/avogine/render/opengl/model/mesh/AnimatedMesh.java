package com.avogine.render.opengl.model.mesh;

import org.joml.primitives.AABBf;

import com.avogine.render.model.mesh.Boundable;
import com.avogine.render.model.mesh.data.VertexBuffers;
import com.avogine.render.opengl.*;
import com.avogine.render.opengl.VAO.VAOBuilder.VertexAttrib;
import com.avogine.render.opengl.model.mesh.data.MeshData;

/**
 *
 */
public final class AnimatedMesh extends Mesh implements Boundable {
	
	private final AABBf aabb;
	
	/**
	 * @param meshData
	 */
	public AnimatedMesh(MeshData meshData) {
		super(meshData);
		aabb = meshData.aabb();
	}
	
	@Override
	protected VAO setupVAO(MeshData meshData) {
		try (var vertexBuffers = meshData.vertexBuffers()) {
			if (vertexBuffers instanceof VertexBuffers(var positions, var normals, var tangents, var bitangents, var textureCoordinates, var c, var weights, var boneIds, var indices)) {
				var vertexFormat4f = VertexAttrib.Format.tightlyPackedUnnormalizedFloat(4);
				var vertexFormat3f = VertexAttrib.Format.tightlyPackedUnnormalizedFloat(3);
				var vertexFormat2f = VertexAttrib.Format.tightlyPackedUnnormalizedFloat(2);

				return VAO.gen(vertexArray -> vertexArray
						.bindBufferData(VBO.staticDraw(), positions)
						.enablePointer(0, vertexFormat3f)
						.bindBufferData(VBO.staticDraw(), normals)
						.enablePointer(1, vertexFormat3f)
						.bindBufferData(VBO.staticDraw(), tangents)
						.enablePointer(2, vertexFormat3f)
						.bindBufferData(VBO.staticDraw(), bitangents)
						.enablePointer(3, vertexFormat3f)
						.bindBufferData(VBO.staticDraw(), textureCoordinates)
						.enablePointer(4, vertexFormat2f)
						.bindBufferData(VBO.staticDraw(), weights)
						.enablePointer(5, vertexFormat4f)
						.bindBufferData(VBO.staticDraw(), boneIds)
						.enablePointer(6, vertexFormat4f)
						.bindElements(indices));
			} else {
				throw new IllegalArgumentException("Record deconstruction failed. VertexBuffers not found.");
			}
		}
	}

	@Override
	public AABBf getAABB() {
		return aabb;
	}
}
