package com.avogine.render.opengl;

import static org.lwjgl.opengl.GL15C.*;

import java.nio.*;

import org.lwjgl.opengl.GL15C;

/**
 * 
 * @param id 
 * @param target 
 */
public record VBO(int id, int target) {
	/**
	 * Default data access value when constructing a VBO without specifying usage. Currently, {@link GL15C#GL_STATIC_DRAW STATIC_DRAW}
	 */
	public static final int DEFAULT_USAGE = GL_STATIC_DRAW;

	/**
	 * Validate the VBO ID corresponds to a real buffer object.
	 */
	public VBO {
		if (!glIsBuffer(id)) {
			throw new IllegalArgumentException("Name is not a valid vertex buffer: " + id);
		}
	}
	
	/**
	 * Generate, bind, and create a data store for a new buffer object.
	 * @param <T>
	 * @param target
	 * @param data
	 * @param size
	 * @param usage
	 */
	public <T extends Buffer> VBO(int target, T data, long size, int usage) {
		int vbo = glGenBuffers();
		glBindBuffer(target, vbo);
		
		VBO.createDataStore(target, data, size, usage);
		
		this(vbo, target);
	}
	
	private static <T extends Buffer> VBO arrayBuffer(T data, long size, int usage) {
		return new VBO(GL_ARRAY_BUFFER, data, size, usage);
	}
	
	/**
	 * @param <T>
	 * @param data
	 * @param usage
	 * @return a new {@link VBO} targeting {@code GL_ARRAY_BUFFER} with created data store initialized with {@code data} and data access of type {@code usage}.
	 */
	public static <T extends Buffer> VBO arrayBuffer(T data, int usage) {
		return arrayBuffer(data, VBO.getBufferSizeInBytes(data), usage);
	}

	/**
	 * @param size
	 * @param usage
	 * @return a new {@link VBO} targeting {@code GL_ARRAY_BUFFER} with created data store initialized with size {@code size} bytes and data access of type {@code usage}.
	 */
	public static VBO arrayBuffer(long size, int usage) {
		return arrayBuffer(null, size, usage);
	}
	
	private static <T extends Buffer> VBO arrayBuffer(T data, long size) {
		return arrayBuffer(data, size, DEFAULT_USAGE);
	}
	
	/**
	 * @param <T>
	 * @param data
	 * @return a new {@link VBO} targeting {@code GL_ARRAY_BUFFER} with created data store initialized with {@code data} and data access of type {@link VBO#DEFAULT_USAGE}.
	 */
	public static <T extends Buffer> VBO arrayBuffer(T data) {
		return arrayBuffer(data, VBO.getBufferSizeInBytes(data));
	}
	
	/**
	 * @param size
	 * @return a new {@link VBO} targeting {@code GL_ARRAY_BUFFER} with created data store initialized with size {@code size} bytes and data access of type {@link VBO#DEFAULT_USAGE}.
	 */
	public static VBO arrayBuffer(long size) {
		return arrayBuffer(null, size);
	}
	
	private static VBO elementArrayBuffer(IntBuffer data, long size, int usage) {
		return new VBO(GL_ELEMENT_ARRAY_BUFFER, data, size, usage);
	}
	
	/**
	 * @param data
	 * @param usage
	 * @return a new {@link VBO} targeting {@code GL_ELEMENT_ARRAY_BUFFER} with created data store initialized with {@code data} and data access of type {@code usage}.
	 */
	public static VBO elementArrayBuffer(IntBuffer data, int usage) {
		return elementArrayBuffer(data, VBO.getBufferSizeInBytes(data), usage);
	}
	
	/**
	 * @param size
	 * @param usage
	 * @return a new {@link VBO} targeting {@code GL_ELEMENT_ARRAY_BUFFER} with created data store initialized with size {@code size} bytes and data access of type {@code usage}.
	 */
	public static VBO elementArrayBuffer(long size, int usage) {
		return elementArrayBuffer(null, size, usage);
	}
	
	private static VBO elementArrayBuffer(IntBuffer data, long size) {
		return elementArrayBuffer(data, size, DEFAULT_USAGE);
	}
	
	/**
	 * @param data
	 * @return a new {@link VBO} targeting {@code GL_ELEMENT_ARRAY_BUFFER} with created data store initialized with {@code data} and data access of type {@link VBO#DEFAULT_USAGE}.
	 */
	public static VBO elementArrayBuffer(IntBuffer data) {
		return elementArrayBuffer(data, VBO.getBufferSizeInBytes(data));
	}
	
	/**
	 * @param size
	 * @return a new {@link VBO} targeting {@code GL_ELEMENT_ARRAY_BUFFER} with created data store initialized with size {@code size} bytes and data access of type {@link VBO#DEFAULT_USAGE}.
	 */
	public static VBO elementArrayBuffer(long size) {
		return elementArrayBuffer(null, size);
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		glDeleteBuffers(id);
	}
	
	/**
	 * 
	 */
	public void bind() {
		glBindBuffer(target, id);
	}
	
	/**
	 * 
	 */
	public void unbind() {
		glBindBuffer(target, 0);
	}
	
	/**
	 * @param <T>
	 * @param data the data to copy into the buffer's data store, or null.
	 * @param size the size in bytes of the buffer object's new data store.
	 * @param usage 
	 */
	public <T extends Buffer> void bufferData(T data, long size, int usage) {
		createDataStore(target, data, size, usage);
	}
	
	/**
	 * @param <T> The type of {@link Buffer} containing the data.
	 * @param offset the offset into the buffer object's data store where data replacement will begin, measured in bytes.
	 * @param data the data to copy into the buffer's data store, or null.
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
	 * @param data the data to copy into the buffer's data store, or null, starting at offset 0.
	 */
	public <T extends Buffer> void bufferSubData(T data) {
		bufferSubData(0, data);
	}
	
	/**
	 * @see {@link GL15C#glBufferData}
	 */
	private static <T extends Buffer> void createDataStore(int target, T data, long size, int usage) {
		switch (data) {
			case ByteBuffer b -> glBufferData(target, b, usage);
			case DoubleBuffer d -> glBufferData(target, d, usage);
			case FloatBuffer f -> glBufferData(target, f, usage);
			case IntBuffer i -> glBufferData(target, i, usage);
			case LongBuffer l -> glBufferData(target, l, usage);
			case ShortBuffer s -> glBufferData(target, s, usage);
			case null -> glBufferData(target, size, usage);
			default -> throw new IllegalArgumentException("Cannot buffer data of type " + data.getClass());
		}
	}
	
	private static long getBufferSizeInBytes(Buffer buffer) {
		return switch (buffer) {
			case ByteBuffer b -> b.limit();
			case DoubleBuffer d -> d.limit() * Double.BYTES;
			case FloatBuffer f -> f.limit() * Float.BYTES;
			case IntBuffer i -> i.limit() * Integer.BYTES;
			case LongBuffer l -> l.limit() * Long.BYTES;
			case ShortBuffer s -> s.limit() * Short.BYTES;
			case null, default -> 0;
		};
	}
}
