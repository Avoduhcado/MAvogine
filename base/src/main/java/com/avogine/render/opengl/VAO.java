package com.avogine.render.opengl;

import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL30C.*;

import java.util.*;

import com.avogine.render.opengl.model.mesh.data.*;

/**
 * 
 * @param id 
 */
public record VAO(int id) {
	/**
	 * 
	 */
	public VAO {
		if (!glIsVertexArray(id)) {
			throw new IllegalArgumentException("Name is not a valid vertex array object: " + id);
		}
	}
	
	/**
	 * @param vertexAttribs
	 * @param index
	 */
	public VAO(Set<Vertex> vertexAttribs, Index index) {
		int vao = glGenVertexArrays();
		glBindVertexArray(vao);
		
		for (Vertex vertex : vertexAttribs) {
			if (Objects.requireNonNull(vertex.buffer(), "Vertex buffer object is null").target() != GL_ARRAY_BUFFER) {
				throw new IllegalArgumentException("Vertex must target: " + GL_ARRAY_BUFFER + " but was: " + vertex.buffer().target());
			}
			vertex.buffer().bind();
			for (Vertex.VertexAttrib attrib : vertex.attribs()) {
				attrib.enable();
			}
		}
		
		if (Objects.nonNull(index)) {
			if (Objects.requireNonNull(index.buffer(), "Index buffer object is null").target() != GL_ELEMENT_ARRAY_BUFFER) {
				throw new IllegalArgumentException("Index must target: " + GL_ELEMENT_ARRAY_BUFFER + " but was: " + index.buffer().target());
			}
			index.buffer().bind();
		}
		
		glBindVertexArray(0);
		
		this(vao);
	}
	
	/**
	 * @param vertexAttribs
	 */
	public VAO(Set<Vertex> vertexAttribs) {
		this(vertexAttribs, null);
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		glDeleteVertexArrays(id);
	}
	
	/**
	 * 
	 */
	public void bind() {
		glBindVertexArray(id);
	}
	
	/**
	 * 
	 */
	public void unbind() {
		glBindVertexArray(0);
	}
	
}
