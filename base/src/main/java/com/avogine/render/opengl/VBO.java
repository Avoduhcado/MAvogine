package com.avogine.render.opengl;

import static org.lwjgl.opengl.GL15.*;

import java.nio.*;

import org.lwjgl.system.MemoryUtil;

/**
 * A wrapper class of OpenGL's Vertex Buffer Object.
 * 
 * @param id the object ID of the vertex buffer.
 * @param target the target to which the buffer object is bound. This target will be re-used by any subsequent bind calls.
 * @param usage the expected usage pattern of the data store. This usage will be re-used by any subsequent bufferData calls.
 * @see <a href="https://www.khronos.org/opengl/wiki/Buffer_Object">Buffer Object</a>
 */
public record VBO(int id, int target, int usage) {

	/**
	 * @param target the target to which the buffer object is bound. This target will be re-used by any subsequent bind calls.
	 * @param usage the expected usage pattern of the data store. This usage will be re-used by any subsequent bufferData calls.
	 */
	public VBO(int target, int usage) {
		this(glGenBuffers(), target, usage);
	}
	
	/**
	 * @param usage the expected usage pattern of the data store. This usage will be re-used by any subsequent bufferData calls.
	 */
	public VBO(int usage) {
		this(GL_ARRAY_BUFFER, usage);
	}
	
	/**
	 * @return an array buffer configured for static drawing.
	 */
	public static VBO staticDraw() {
		return new VBO(GL_ARRAY_BUFFER, GL_STATIC_DRAW);
	}
	
	/**
	 * @return an element array buffer configured for static drawing.
	 */
	public static VBO elementArrayBuffer() {
		return new VBO(GL_ELEMENT_ARRAY_BUFFER, GL_STATIC_DRAW);
	}
	
	/**
	 * Static convenient method to clear the currently bound array buffer.
	 */
	public static void unbind() {
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	/**
	 * Delete this buffer object.
	 */
	public void cleanup() {
		glDeleteBuffers(id);
	}
	
	/**
	 * Bind this named buffer object.
	 */
	public void bind() {
		glBindBuffer(target, id);
	}
	

	/**
	 * @param size
	 */
	public void bufferData(long size) {
		glBufferData(target, size, usage);
	}
	
	/**
	 * @param <T>
	 * @param data the data to copy into the buffer's data store, or null
	 */
	public <T extends Buffer> void bufferData(T data) {
		switch (data) {
			case ByteBuffer b -> glBufferData(target, b, usage);
			case DoubleBuffer d -> glBufferData(target, d, usage);
			case FloatBuffer f -> glBufferData(target, f, usage);
			case IntBuffer i -> glBufferData(target, i, usage);
			case LongBuffer l -> glBufferData(target, l, usage);
			case ShortBuffer s -> glBufferData(target, s, usage);
			case null -> glBufferData(target, MemoryUtil.NULL, usage);
			default -> throw new IllegalArgumentException("Cannot buffer data of type " + data.getClass());
		}
	}
	
	/**
	 * @param <T>
	 * @param offset
	 * @param data
	 */
	public <T extends Buffer> void bufferSubData(long offset, T data) {
		switch (data) {
			case ByteBuffer b -> glBufferSubData(target, offset, b);
			case DoubleBuffer d -> glBufferSubData(target, offset, d);
			case FloatBuffer f -> glBufferSubData(target, offset, f);
			case IntBuffer i -> glBufferSubData(target, offset, i);
			case LongBuffer l -> glBufferSubData(target, offset, l);
			case ShortBuffer s -> glBufferSubData(target, offset, s);
			default -> throw new IllegalArgumentException("Cannot buffer sub data of type " + data.getClass());
		}
	}
	
	/**
	 * @param <T>
	 * @param data
	 */
	public <T extends Buffer> void bufferSubData(T data) {
		bufferSubData(0, data);
	}
}
