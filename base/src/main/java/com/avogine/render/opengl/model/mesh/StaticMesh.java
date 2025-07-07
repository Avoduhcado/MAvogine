package com.avogine.render.opengl.model.mesh;

import static org.lwjgl.opengl.GL11.*;

import org.joml.primitives.AABBf;

import com.avogine.render.model.mesh.MeshData;
import com.avogine.render.model.mesh.data.VertexBuffers;
import com.avogine.render.model.mesh.parameters.Bound3D;
import com.avogine.render.opengl.*;
import com.avogine.render.opengl.model.StaticModel;

/**
 * Used by {@link StaticModel}.
 */
public class StaticMesh extends Mesh<MeshData> implements Bound3D {
	
	protected AABBf aabb;
	
	/**
	 * @param meshData
	 */
	public StaticMesh(MeshData meshData) {
		super(meshData);
		this.aabb = meshData.getAabb();
	}
	
	@Override
	public VAO buildVertexArray(MeshData meshData) {
		try (VertexBuffers vertexBuffers = meshData.getVertexBuffers()) {
			var vertexAttrib3f = VertexAttrib.Pointer.tightlyPackedUnnormalizedFloat(3);
			return VAO.gen().bind()
					.vertexBuffer(VBO.gen().bind().bufferData(vertexBuffers.positions())
							.enable(VertexAttrib.array(0)
									.pointer(vertexAttrib3f)))
					.vertexBuffer(VBO.gen().bind().bufferData(vertexBuffers.normals())
							.enable(VertexAttrib.array(1)
									.pointer(vertexAttrib3f)))
					.vertexBuffer(VBO.gen().bind().bufferData(vertexBuffers.tangents())
							.enable(VertexAttrib.array(2)
									.pointer(vertexAttrib3f)))
					.vertexBuffer(VBO.gen().bind().bufferData(vertexBuffers.bitangents())
							.enable(VertexAttrib.array(3)
									.pointer(vertexAttrib3f)))
					.vertexBuffer(VBO.gen().bind().bufferData(vertexBuffers.textureCoordinates())
							.enable(VertexAttrib.array(4)
									.pointer(VertexAttrib.Pointer.tightlyPackedUnnormalizedFloat(2))))
					.vertexBuffer(VBO.genEBO().bind().bufferData(vertexBuffers.indices()));
		} finally {
			VAO.unbind();
		}
	}

	@Override
	public void draw() {
		glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
	}

	@Override
	public AABBf getAABB() {
		return aabb;
	}
}
