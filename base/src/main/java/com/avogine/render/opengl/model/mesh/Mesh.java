package com.avogine.render.opengl.model.mesh;

import static org.lwjgl.opengl.GL11.*;

import org.joml.primitives.AABBf;

import com.avogine.render.model.mesh.Boundable;
import com.avogine.render.model.mesh.data.VertexBuffers;
import com.avogine.render.opengl.*;
import com.avogine.render.opengl.model.mesh.data.MeshData;

/**
 *  
 */
public class Mesh extends VertexArrayObject<MeshData> implements Boundable {
	
	private final AABBf aabb;
	
	/**
	 * @param meshData
	 */
	public Mesh(MeshData meshData) {
		super(meshData);
		aabb = meshData.aabb();
	}

	@Override
	protected int generateVertexArray(MeshData vertexData) {
		try (VertexBuffers vertexBuffers = vertexData.vertexBuffers()) {
			var vertexAttrib3f = VertexAttrib.Pointer.tightlyPackedUnnormalizedFloat(3);
			int vaoID = VAO.gen().bind().id();
			getVertexBufferObjects().add(VBO.gen().bind().bufferData(vertexBuffers.positions())
							.enable(VertexAttrib.array(0)
									.pointer(vertexAttrib3f)).id());
			getVertexBufferObjects().add(VBO.gen().bind().bufferData(vertexBuffers.normals())
					.enable(VertexAttrib.array(1)
							.pointer(vertexAttrib3f)).id());
			getVertexBufferObjects().add(VBO.gen().bind().bufferData(vertexBuffers.tangents())
					.enable(VertexAttrib.array(2)
							.pointer(vertexAttrib3f)).id());
			getVertexBufferObjects().add(VBO.gen().bind().bufferData(vertexBuffers.bitangents())
					.enable(VertexAttrib.array(3)
							.pointer(vertexAttrib3f)).id());
			getVertexBufferObjects().add(VBO.gen().bind().bufferData(vertexBuffers.textureCoordinates())
					.enable(VertexAttrib.array(4)
							.pointer(VertexAttrib.Pointer.tightlyPackedUnnormalizedFloat(2))).id());
			getVertexBufferObjects().add(VBO.genEBO().bind().bufferData(vertexBuffers.indices()).id());
			
			return vaoID;
		} finally {
			VAO.unbind();
		}
	}

	/**
	 * Render this mesh.
	 */
	public void draw() {
		glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
	}

	@Override
	public AABBf getAABB() {
		return aabb;
	}
	
}
