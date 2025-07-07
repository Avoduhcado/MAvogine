package com.avogine.render.opengl.model.mesh;

import static org.lwjgl.opengl.GL30.*;

import java.util.*;

import org.lwjgl.opengl.GL30;

import com.avogine.render.opengl.model.mesh.data.VertexData;

/**
 * @param <T> 
 */
public abstract class VertexArrayObject<T extends VertexData> {

	private final int id;
	private final List<Integer> vertexBufferObjects;
	
	private final int vertexCount;
	
	protected VertexArrayObject(T vertexData) {
		vertexBufferObjects = new ArrayList<>();
		vertexCount = vertexData.getVertexCount();
		id = generateVertexArray(vertexData);
	}
	
	/**
	 * Free VBOs and VAO.
	 */
	public void cleanup() {
		vertexBufferObjects.forEach(GL30::glDeleteBuffers);
		glDeleteVertexArrays(id);
	}
	
	/**
	 * @return
	 */
	protected abstract int generateVertexArray(T vertexData);
	
	/**
	 * Bind this VAO.
	 */
	public void bind() {
		glBindVertexArray(id);
	}
	
	/**
	 * Render this VAO.
	 */
	public abstract void draw();
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @return the vertexBufferObjects
	 */
	public List<Integer> getVertexBufferObjects() {
		return vertexBufferObjects;
	}
	
	/**
	 * @return the vertexCount
	 */
	public int getVertexCount() {
		return vertexCount;
	}
	
}
