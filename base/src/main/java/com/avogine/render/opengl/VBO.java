package com.avogine.render.opengl;

import static org.lwjgl.opengl.GL15.*;

import java.nio.*;

import org.lwjgl.opengl.GL15;

/**
 * @param id 
 * @param target 
 */
public record VBO(int id, int target) {
	
	/**
	 * @param target 
	 * @return a new Vertex Buffer Object wrapper generated with {@link GL15#glGenBuffers()} with a specific buffer target.
	 */
	public static VBO gen(int target) {
		return new VBO(glGenBuffers(), target);
	}
	
	/**
	 * @return a new Vertex Buffer Object wrapper generated with {@link GL15#glGenBuffers()}.
	 */
	public static VBO gen() {
		return new VBO(glGenBuffers(), GL_ARRAY_BUFFER);
	}

	/**
	 * @return a new Vertex Buffer Object wrapper specifically bound to {@link GL15#GL_ELEMENT_ARRAY_BUFFER}.
	 */
	public static VBO genEBO() {
		return new VBO(glGenBuffers(), GL_ELEMENT_ARRAY_BUFFER);
	}
	
	/**
	 * @param target
	 */
	public static void unbind(int target) {
		glBindBuffer(target, 0);
	}
	
	/**
	 * 
	 */
	public static void unbind() {
		unbind(GL_ARRAY_BUFFER);
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		glDeleteBuffers(id);
	}
	
	/**
	 * @return this
	 */
	public VBO bind() {
		glBindBuffer(target, id);
		return this;
	}
	
	/**
	 * @param size
	 * @param usage
	 * @return this
	 */
	public VBO bufferData(long size, int usage) {
		glBufferData(target, size, usage);
		return this;
	}
	
	/**
	 * @param <T>
	 * @param data
	 * @param usage
	 * @return this
	 */
	public <T extends Buffer> VBO bufferData(T data, int usage) {
		switch (data) {
			case ByteBuffer b -> glBufferData(target, b, usage);
			case DoubleBuffer d -> glBufferData(target, d, usage);
			case FloatBuffer f -> glBufferData(target, f, usage);
			case IntBuffer i -> glBufferData(target, i, usage);
			case LongBuffer l -> glBufferData(target, l, usage);
			case ShortBuffer s -> glBufferData(target, s, usage);
			case null -> glBufferData(target, (ByteBuffer) data, usage);
			default -> throw new IllegalArgumentException("Cannot buffer data of type " + data.getClass());
		}
		return this;
	}
	
	/**
	 * @param <T>
	 * @param data
	 * @return this
	 */
	public <T extends Buffer> VBO bufferData(T data) {
		return bufferData(data, GL_STATIC_DRAW);
	}
	
	/**
	 * @param <T>
	 * @param offset
	 * @param data
	 * @return this
	 */
	public <T extends Buffer> VBO bufferSubData(long offset, T data) {
		switch (data) {
			case ByteBuffer b -> glBufferSubData(target, offset, b);
			case DoubleBuffer d -> glBufferSubData(target, offset, d);
			case FloatBuffer f -> glBufferSubData(target, offset, f);
			case IntBuffer i -> glBufferSubData(target, offset, i);
			case LongBuffer l -> glBufferSubData(target, offset, l);
			case ShortBuffer s -> glBufferSubData(target, offset, s);
			case null -> glBufferSubData(target, offset, (ByteBuffer) data);
			default -> throw new IllegalArgumentException("Cannot buffer data of type " + data.getClass());
		}
		return this;
	}
	
	/**
	 * @param <T>
	 * @param data
	 * @return this
	 */
	public <T extends Buffer> VBO bufferSubData(T data) {
		return bufferSubData(0, data);
	}
}
