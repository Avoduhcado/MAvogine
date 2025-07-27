package com.avogine.render.opengl;

import static org.lwjgl.opengl.GL30.*;

import java.nio.Buffer;
import java.util.*;
import java.util.function.Supplier;

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
	 * @param vboBuilders
	 * @return a new Vertex Array Object wrapper generated with {@link GL30#glGenVertexArrays()} and populated with the supplied VBOs.
	 */
	@SafeVarargs
	public static VAO gen(Supplier<VBO>...vboBuilders) {
		int id = glGenVertexArrays();
		glBindVertexArray(id);
		List<VBO> vboList = Arrays.stream(vboBuilders).map(Supplier<VBO>::get).toList();
		return new VAO(id, vboList);
	}
	
	/**
	 * Static convenience method for un-binding whichever VAO is currently bound.
	 */
	public static void unbind() {
		glBindVertexArray(0);
	}
	
	/**
	 * Delete all buffer objects and the array object itself.
	 */
	public void cleanup() {
		vertexBuffers.forEach(VBO::cleanup);
		glDeleteVertexArrays(id);
	}
	
	/**
	 * @return this
	 */
	public VAO bind() {
		glBindVertexArray(id);
		return this;
	}
	
	/**
	 *
	 * @param <T>
	 * @param data
	 * @param vertexAttribArray
	 * @param pointer
	 */
	public static record VBOBuilder<T extends Buffer>(T data, int vertexAttribArray, VertexAttrib.Pointer pointer) implements Supplier<VBO> {
		@Override
		public VBO get() {
			var vbo = VBO.gen().bind().bufferData(data);
			VertexAttrib.array(vertexAttribArray).pointer(pointer).enable();
			return vbo;
		}
	}
}
