package com.avogine.render.data.mesh;

import com.avogine.render.data.gl.VAO;
import com.avogine.render.data.vertices.VertexArrayData;

/**
 * @param <T> 
 */
public abstract class Mesh<T extends VertexArrayData> {

	protected int vertexCount;
	
	protected final VAO vao;
	
	protected Mesh(int vertexCount, T vertexData) {
		this.vertexCount = vertexCount;
		this.vao = setupData(vertexData);
	}
	
	/**
	 * @param vertexData
	 */
	protected VAO setupData(T vertexData) {
		return vertexData.bind();
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
