package com.avogine.render.data.mesh;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.*;
import java.util.*;
import org.lwjgl.system.*;

import com.avogine.render.data.*;
import com.avogine.render.data.mesh.Texture.*;

/**
 *
 */
public class Mesh {

	private List<Vertex> vertices;
	private List<Integer> indices;
	private List<Texture> textures;
	
	private int vao;
	private int vbo;
	private int ebo;
	
	// TODO Implement this better
	public Material material;
	
	/**
	 * @param vertices
	 * @param indices
	 * @param textures
	 */
	public Mesh(List<Vertex> vertices, List<Integer> indices, List<Texture> textures) {
		this.vertices = vertices;
		this.indices = indices;
		this.textures = textures;
		
		setupMesh();
	}
	
	private void setupMesh() {
		vao = glGenVertexArrays();
		vbo = glGenBuffers();
		ebo = glGenBuffers();
		
		FloatBuffer vertexData = allocateVertexData();
		
		glBindVertexArray(vao);
		
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);
		MemoryUtil.memFree(vertexData);
		
		IntBuffer indexData = allocateIndexData();

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexData, GL_STATIC_DRAW);
		MemoryUtil.memFree(indexData);
		
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3L * Float.BYTES);
		glEnableVertexAttribArray(2);
		glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.BYTES, 6L * Float.BYTES);
		
		glBindVertexArray(0);
	}
	
	private FloatBuffer allocateVertexData() {
		FloatBuffer vertexData = MemoryUtil.memAllocFloat(vertices.size() * Vertex.ATTRIBUTE_SIZE);
		float[] attributeArray = new float[Vertex.ATTRIBUTE_SIZE];
		
		vertices.forEach(vertex -> vertexData.put(vertex.getAttributes(attributeArray), 0, Vertex.ATTRIBUTE_SIZE));
		
		vertexData.flip();
		
		return vertexData;
	}
	
	private IntBuffer allocateIndexData() {
		IntBuffer indexData = MemoryUtil.memAllocInt(indices.size());
		indices.forEach(indexData::put);
		indexData.flip();
		
		return indexData;
	}
	
	/**
	 * 
	 */
	public void render() {
		int diffuseN = 0;
		int specularN = 0;
		for (int i = 0; i < textures.size(); i++) {
			Texture texture = textures.get(i);
			
			TextureType type = texture.getType();
			if (type == TextureType.DIFFUSE) {
				glActiveTexture(GL_TEXTURE0 + diffuseN++);
			} else if (type == TextureType.SPECULAR) {
				glActiveTexture(GL_TEXTURE4 + specularN++);
			}
			
			glBindTexture(GL_TEXTURE_2D, texture.getId());
		}
		
		glBindVertexArray(vao);
		glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
		glBindVertexArray(0);
		
		// Resetting texture to default. Does this handle unbinding? Will the next render use leftover diffuse/specular textures?
		glActiveTexture(GL_TEXTURE0);
	}
	
	/**
	 * XXX Should this return vertex.size instead?
	 * @return the number of vertices to render in a single draw call
	 */
	public int getVertexCount() {
		return indices.size();
	}
	
	/**
	 * Free all GPU memory.
	 */
	public void cleanup() {
		glDeleteVertexArrays(vao);
		glDeleteBuffers(vbo);
		glDeleteBuffers(ebo);
		// TODO Somehow handle freeing up textures?
	}
	
}
