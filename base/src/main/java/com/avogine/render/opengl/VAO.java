package com.avogine.render.opengl;

import static org.lwjgl.opengl.GL30C.*;

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
	 * Generates and binds a new vertex array object.
	 */
	public VAO() {
		int name = glGenVertexArrays();
		glBindVertexArray(name);
		this(name);
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
