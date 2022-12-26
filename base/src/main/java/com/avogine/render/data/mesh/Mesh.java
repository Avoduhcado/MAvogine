package com.avogine.render.data.mesh;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.*;

import org.lwjgl.opengl.GL11;

/**
 * Used by {@link Model}.
 */
public class Mesh {
	
	private int vao;
	private int vbo;
	private int ebo;
	
	private int indexSize;
	
	// TODO Implement this better
	private int materialIndex;
	
	/**
	 * @param vertexBuffer 
	 * @param indexBuffer 
	 * @param materialIndex 
	 */
	public Mesh(FloatBuffer vertexBuffer, IntBuffer indexBuffer, int materialIndex) {
		this.indexSize = indexBuffer.limit();
		this.materialIndex = materialIndex;
		
		setupMesh(vertexBuffer, indexBuffer);
	}
	
	private void setupMesh(FloatBuffer vertexBuffer, IntBuffer indexBuffer) {
		vao = glGenVertexArrays();
		vbo = glGenBuffers();
		ebo = glGenBuffers();

		glBindVertexArray(vao);

		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3L * Float.BYTES);
		glEnableVertexAttribArray(2);
		glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.BYTES, 6L * Float.BYTES);

		glBindVertexArray(0);
	}
	
	/**
	 * 
	 */
	public void render() {
		glBindVertexArray(vao);
		glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
		glBindVertexArray(0);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	/**
	 * XXX Should this return vertex.size instead?
	 * @return the number of vertices to render in a single draw call
	 */
	private int getVertexCount() {
		return indexSize;
	}
	
	/**
	 * @return the materialIndex
	 */
	public int getMaterialIndex() {
		return materialIndex;
	}
	
	/**
	 * @param materialIndex the materialIndex to set
	 */
	public void setMaterialIndex(int materialIndex) {
		this.materialIndex = materialIndex;
	}
	
	/**
	 * Free all GPU memory.
	 */
	public void cleanup() {
		glDeleteBuffers(vbo);
		glDeleteBuffers(ebo);
		glDeleteVertexArrays(vao);
	}
	
}
