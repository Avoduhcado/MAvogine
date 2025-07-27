package com.avogine.render.opengl.model.mesh;

import static org.lwjgl.opengl.GL11.*;

import org.joml.primitives.AABBf;

import com.avogine.render.model.mesh.Boundable;
import com.avogine.render.model.mesh.data.VertexBuffers;
import com.avogine.render.opengl.*;
import com.avogine.render.opengl.VAO.VBOBuilder;
import com.avogine.render.opengl.model.mesh.data.MeshData;

/**
 * 
 */
public class Mesh implements Renderable, Boundable {
	
	private final AABBf aabb;
	private int vertexCount;
	private VAO vao;

	protected Mesh(AABBf aabb, int vertexCount, VAO vao) {
		this.aabb = aabb;
		this.vertexCount = vertexCount;
		this.vao = vao;
	}
	
	/**
	 * @param meshData
	 */
	public Mesh(MeshData meshData) {
		this(meshData.aabb(), meshData.getVertexCount(), initVAO(meshData));
	}
	
	private static VAO initVAO(MeshData meshData) {
		try (VertexBuffers vertexBuffers = meshData.vertexBuffers()) {
			if (vertexBuffers instanceof VertexBuffers(var positions, var normals, var tangents, var bitangents, var textureCoordinates, var c, var w, var b, var indices)) {
				var vertexAttrib3f = VertexAttrib.Pointer.tightlyPackedUnnormalizedFloat(3);

				return VAO.gen(
						new VBOBuilder<>(positions, 0, vertexAttrib3f), 
						new VBOBuilder<>(normals, 1, vertexAttrib3f),
						new VBOBuilder<>(tangents, 2, vertexAttrib3f),
						new VBOBuilder<>(bitangents, 3, vertexAttrib3f),
						new VBOBuilder<>(textureCoordinates, 4, VertexAttrib.Pointer.tightlyPackedUnnormalizedFloat(2)),
						() -> VBO.genEBO().bind().bufferData(indices));
			} else {
				throw new IllegalArgumentException("Record deconstruction failed.");
			}
		} finally {
			VAO.unbind();
		}
	}
	
	@Override
	public void cleanup() {
		vao.cleanup();
	}
	
	@Override
	public void bind() {
		vao.bind();
	}
	
	@Override
	public void draw() {
		glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
	}

	@Override
	public AABBf getAABB() {
		return aabb;
	}

	/**
	 * @return the vertexCount
	 */
	@Override
	public int getVertexCount() {
		return vertexCount;
	}
	
	/**
	 * @return the vao
	 */
	@Override
	public VAO getVAO() {
		return vao;
	}
	
}
