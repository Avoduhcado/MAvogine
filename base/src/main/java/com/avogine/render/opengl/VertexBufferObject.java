package com.avogine.render.opengl;

import static org.lwjgl.opengl.GL15.*;

import java.nio.*;

/**
 * A wrapper class of OpenGL's Vertex Buffer Object.
 * 
 * @param id the object ID of the vertex buffer.
 * @param target the target to which the buffer object is bound. This target will be re-used by any subsequent bind calls.
 * @param usage the expected usage pattern of the data store. This usage will be re-used by any subsequent bufferData calls.
 * @see <a href="https://www.khronos.org/opengl/wiki/Buffer_Object">Buffer Object</a>
 */
public record VertexBufferObject(int id, int target, int usage) {
	/**
	 * @param <T>
	 * @param data
	 * @return an array buffer with its data store filled and configured for static draw usage.
	 */
	public static <T extends Buffer> VertexBufferObject arrayBufferStaticDraw(T data) {
		return new VertexBufferObject(glGenBuffers(), GL_ARRAY_BUFFER, GL_STATIC_DRAW).bind().bufferData(data);
	}
	
	/**
	 * 
	 * @param <T>
	 * @param usage
	 * @param data
	 * @return an array buffer with its data store filled and configured for the given usage.
	 */
	public static <T extends Buffer> VertexBufferObject arrayBufferWithUsage(int usage, T data) {
		return new VertexBufferObject(glGenBuffers(), GL_ARRAY_BUFFER, usage).bind().bufferData(data);
	}
	
	/**
	 * @param indices
	 * @return an element array buffer with its data stored filled with the given index buffer.
	 */
	public static VertexBufferObject elementBuffer(IntBuffer indices) {
		return new VertexBufferObject(glGenBuffers(), GL_ELEMENT_ARRAY_BUFFER, GL_STATIC_DRAW).bind().bufferData(indices);
	}
	
	/**
	 * @param target the target to which the buffer object is bound. This target will be re-used by any subsequent bind calls.
	 * @param usage the expected usage pattern of the data store. This usage will be re-used by any subsequent bufferData calls.
	 */
	public VertexBufferObject(int target, int usage) {
		this(glGenBuffers(), target, usage);
	}
	
	/**
	 * @param usage the expected usage pattern of the data store. This usage will be re-used by any subsequent bufferData calls.
	 */
	public VertexBufferObject(int usage) {
		this(GL_ARRAY_BUFFER, usage);
	}
	
	/**
	 * 
	 */
	public VertexBufferObject() {
		this(GL_STATIC_DRAW);
	}
	
	/**
	 * Delete this buffer object.
	 */
	public void cleanup() {
		glDeleteBuffers(id);
	}
	
	/**
	 * @return this
	 */
	public VertexBufferObject bind() {
		glBindBuffer(target, id);
		return this;
	}
	
	/**
	 * @param size
	 * @return this
	 */
	public VertexBufferObject bufferData(long size) {
		glBufferData(target, size, usage);
		return this;
	}
	
	/**
	 * @param <T>
	 * @param data
	 * @return this
	 */
	public <T extends Buffer> VertexBufferObject bufferData(T data) {
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
	 * @param offset
	 * @param data
	 * @return this
	 */
	public <T extends Buffer> VertexBufferObject bufferSubData(long offset, T data) {
		switch (data) {
			case ByteBuffer b -> glBufferSubData(target, offset, b);
			case DoubleBuffer d -> glBufferSubData(target, offset, d);
			case FloatBuffer f -> glBufferSubData(target, offset, f);
			case IntBuffer i -> glBufferSubData(target, offset, i);
			case LongBuffer l -> glBufferSubData(target, offset, l);
			case ShortBuffer s -> glBufferSubData(target, offset, s);
			case null -> glBufferSubData(target, offset, (ByteBuffer) data);
			default -> throw new IllegalArgumentException("Cannot buffer sub data of type " + data.getClass());
		}
		return this;
	}
	
	/**
	 * @param <T>
	 * @param data
	 * @return this
	 */
	public <T extends Buffer> VertexBufferObject bufferSubData(T data) {
		return bufferSubData(0, data);
	}
}
