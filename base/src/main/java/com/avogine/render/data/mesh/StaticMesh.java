package com.avogine.render.data.mesh;

import static org.lwjgl.opengl.GL11.*;

import org.joml.primitives.AABBf;

import com.avogine.render.data.*;
import com.avogine.render.data.gl.*;
import com.avogine.render.data.mesh.parameters.Bound3D;
import com.avogine.render.data.model.StaticModel;

/**
 * Used by {@link StaticModel}.
 * <p>
 * TODO#39 <a href="https://github.com/Avoduhcado/MAvogine/issues/39">Animated models #39</a>
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
					.addBuffer(VBO.gen().bind()
							.bufferData(vertexBuffers.positions()).enable(VertexAttrib.array(0).pointer(vertexAttrib3f)))
					.addBuffer(VBO.gen().bind()
							.bufferData(vertexBuffers.normals()).enable(VertexAttrib.array(1).pointer(vertexAttrib3f)))
					.addBuffer(VBO.gen().bind()
							.bufferData(vertexBuffers.tangents()).enable(VertexAttrib.array(2).pointer(vertexAttrib3f)))
					.addBuffer(VBO.gen().bind()
							.bufferData(vertexBuffers.bitangents()).enable(VertexAttrib.array(3).pointer(vertexAttrib3f)))
					.addBuffer(VBO.gen().bind()
							.bufferData(vertexBuffers.textureCoordinates()).enable(VertexAttrib.array(4).pointer(VertexAttrib.Pointer.tightlyPackedUnnormalizedFloat(2))))
					.addBuffer(VBO.genEBO().bind()
							.bufferData(vertexBuffers.indices()));
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
