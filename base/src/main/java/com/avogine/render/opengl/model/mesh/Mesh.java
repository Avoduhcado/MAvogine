package com.avogine.render.opengl.model.mesh;

import com.avogine.render.model.mesh.MeshData;
import com.avogine.render.opengl.VAO;
import com.avogine.render.opengl.model.mesh.data.VertexArrayBuilder;

/**
 * @param <T> 
 */
public abstract class Mesh<T extends MeshData> implements VertexArrayBuilder<T> {

	protected int vertexCount;
	
	protected final VAO vao;
	
	protected Mesh(T meshData) {
		this.vertexCount = meshData.getVertexCount();
		this.vao = buildVertexArray(meshData);
	}
	
	/**
	 * Free this Mesh's {@link VAO}.
	 */
	public void cleanup() {
		vao.cleanup();
	}
	
	/**
	 * Render the mesh.
	 */
	public abstract void draw();
	
	/**
	 * @return the {@link VAO} this mesh is bound to.
	 */
	public VAO getVao() {
		return vao;
	}
	
	/**
	 * @return the address this mesh is bound to in GPU memory.
	 */
	public int getVaoId() {
		return vao.id();
	}
	
	/**
	 * @return the number of vertices that make up this {@link Mesh}.
	 */
	public int getVertexCount() {
		return vertexCount;
	}
}
