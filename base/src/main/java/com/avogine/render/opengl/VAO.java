package com.avogine.render.opengl;

import static org.lwjgl.opengl.GL30.*;

import java.util.*;

import org.lwjgl.opengl.GL30;

/**
 * Wrapper class for an OpenGL Vertex Array Object.
 * @param id 
 * @param vertexBuffers 
 */
public record VAO(int id, List<VBO> vertexBuffers) {
	/**
	 * @return a new Vertex Array Object wrapper generated with {@link GL30#glGenVertexArrays()}.
	 */
	public static VAO gen() {
		return new VAO(glGenVertexArrays(), new ArrayList<>());
	}

	/**
	 * 
	 */
	public static void unbind() {
		glBindVertexArray(0);
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		glDeleteVertexArrays(id);
		vertexBuffers.forEach(VBO::cleanup);
	}
	
	/**
	 * @return 
	 */
	public VAO bind() {
		glBindVertexArray(id);
		return this;
	}
	
	/**
	 * @param vertexBuffer 
	 * @return
	 */
	public VAO vertexBuffer(VBO vertexBuffer) {
		vertexBuffers.add(vertexBuffer);
		return this;
	}
	
}
