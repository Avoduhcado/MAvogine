package com.avogine.render.opengl.model.mesh;

import static org.lwjgl.opengl.GL11.*;

import java.util.List;
import java.util.function.Consumer;

import com.avogine.render.opengl.VAO;
import com.avogine.render.opengl.model.mesh.data.MeshData;

/**
 * Parent type of a general Mesh implementation.
 */
public abstract sealed class Mesh permits StaticMesh, InstancedMesh, AnimatedMesh {

	private final VAO vao;
	private final int vertexCount;

	protected Mesh(VAO vao, int vertexCount) {
		this.vao = vao;
		this.vertexCount = vertexCount;
	}
	
	protected Mesh(MeshData meshData) {
		this.vao = setupVAO(meshData);
		this.vertexCount = meshData.getVertexCount();
	}
	
	/**
	 * Free the underlying vertex array object of this mesh.
	 */
	public void cleanup() {
		vao.cleanup();
	}
	
	protected abstract VAO setupVAO(MeshData meshData);
	
	protected void draw() {
		glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
	}
	
	/**
	 * Bind and draw this mesh.
	 */
	public void render() {
		vao.bind();
		draw();
	}
	
	/**
	 * Bind this mesh, and then issue a separate draw for each element after applying the action per element.
	 * 
	 * @param <T> The type of element to process for this bulk render operation.
	 * @param elements A list of elements to process before drawing.
	 * @param action The action to perform on each element before drawing.
	 */
	public <T> void render(List<T> elements, Consumer<T> action) {
		vao.bind();
		for (var element : elements) {
			action.accept(element);
			draw();
		}
	}
	
	protected VAO getVao() {
		return vao;
	}

	/**
	 * @return the number of vertices.
	 */
	public int getVertexCount() {
		return vertexCount;
	}
	
}
