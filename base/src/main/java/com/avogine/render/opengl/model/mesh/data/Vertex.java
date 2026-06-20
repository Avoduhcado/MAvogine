package com.avogine.render.opengl.model.mesh.data;

import static org.lwjgl.system.MemoryUtil.*;

import java.nio.*;
import java.util.function.Consumer;

import com.avogine.render.opengl.VertexArrayObject;
import com.avogine.render.opengl.VertexArrayObject.Builder;
import com.avogine.render.opengl.VertexArrayObject.Builder.*;
import com.avogine.render.opengl.VertexArrayObject.Builder.VertexBuilder.AttribBuilder;
import com.avogine.render.opengl.model.mesh.data.Vertex.*;

public sealed interface Vertex extends Consumer<VertexArrayObject.Builder>, AutoCloseable permits Vertex2D, Vertex3D, Vertex4D, Vertex4x4fInstanced, BoneID {
	/**
	 * @param data
	 * @param location
	 * @return a {@link Vertex2D} wrapping the given data at vertex attribute location.
	 */
	public static Vertex2D wrap2D(Object data, int location) {
		Buffer buffer = switch (data) {
			case int size -> memCalloc(size);
			case float[] floatArray -> memAllocFloat(floatArray.length).put(floatArray).flip();
			case int[] intArray -> memAllocInt(intArray.length).put(intArray).flip();
			default -> throw new IllegalArgumentException("Invalid data: " + data);
		};
		return new Vertex2D(buffer, location);
	}

	/**
	 * @param data
	 * @param location
	 * @return a {@link Vertex3D} wrapping the given data at vertex attribute location.
	 */
	public static Vertex3D wrap3D(Object data, int location) {
		Buffer buffer = switch (data) {
			case int size -> memCalloc(size);
			case float[] floatArray -> memAllocFloat(floatArray.length).put(floatArray).flip();
			case int[] intArray -> memAllocInt(intArray.length).put(intArray).flip();
			default -> throw new IllegalArgumentException("Invalid data: " + data);
		};
		return new Vertex3D(buffer, location);
	}

	/**
	 * @param data
	 * @param location
	 * @return a {@link Vertex4D} wrapping the given data at vertex attribute location.
	 */
	public static Vertex4D wrap4D(Object data, int location) {
		Buffer buffer = switch (data) {
			case int size -> memCalloc(size);
			case float[] floatArray -> memAllocFloat(floatArray.length).put(floatArray).flip();
			case int[] intArray -> memAllocInt(intArray.length).put(intArray).flip();
			default -> throw new IllegalArgumentException("Invalid data: " + data);
		};
		return new Vertex4D(buffer, location);
	}

	@Override
	default void close() {
		memFree(buffer());
	}

	/**
	 * @return a {@link Buffer} of data to bind for this vertex.
	 */
	public Buffer buffer();

	/**
	 * @return the attribute location of this vertex in a {@link VertexArrayObject}.
	 */
	public int location();

	@Override
	public default void accept(Builder builder) {
		builder.vertex(this::bufferData, this::attribute);
	}

	/**
	 * @param builder
	 * @return {@link VertexBufferBuilder}
	 */
	public default VertexBufferBuilder bufferData(VertexBufferBuilder builder) {
		return builder.bufferData(buffer());
	}

	/**
	 * @param attrib
	 */
	public void attribute(VertexBuilder attrib);

	/**
	 *
	 * @param buffer
	 * @param location 
	 */
	public record Vertex2D(Buffer buffer, int location) implements Vertex {
		@Override
		public void attribute(VertexBuilder vertex) {
			vertex.array(location, attrib -> attrib.pointer(p -> p.size(2)));
		}
	}

	/**
	 *
	 * @param buffer
	 * @param location 
	 */
	public record Vertex3D(Buffer buffer, int location) implements Vertex {
		@Override
		public void attribute(VertexBuilder vertex) {
			vertex.array(location, attrib -> attrib.pointer(p -> p.size(3)));
		}
	}

	/**
	 *
	 * @param buffer
	 * @param location 
	 */
	public record Vertex4D(Buffer buffer, int location) implements Vertex {
		@Override
		public void attribute(VertexBuilder vertex) {
			vertex.array(location, AttribBuilder::pointer4f);
		}
	}

	/**
	 *
	 * @param buffer
	 * @param location
	 * @param divisor
	 */
	public record Vertex4x4fInstanced(FloatBuffer buffer, int location, int divisor) implements Vertex {
		private static final int VERTEX_STRIDE = Float.BYTES * 4;

		/**
		 * @param data
		 * @param location
		 * @param divisor
		 * @return a {@link Vertex4x4fInstanced} wrapping the given data at 4 sequential vertex attribute locations.
		 */
		public static Vertex4x4fInstanced wrap(float[] data, int location, int divisor) {
			return new Vertex4x4fInstanced(memAllocFloat(data.length).put(data).flip(), location, divisor);
		}

		/**
		 * @param data
		 * @param location
		 * @return a {@link Vertex4x4fInstanced} wrapping the given data at 4 sequential vertex attribute locations.
		 */
		public static Vertex4x4fInstanced wrap(float[] data, int location) {
			return wrap(data, location, 1);
		}

		/**
		 * @param buffer 
		 * @param location 
		 */
		public Vertex4x4fInstanced(FloatBuffer buffer, int location) {
			this(buffer, location, 1);
		}

		@Override
		public void attribute(VertexBuilder vertex) {
			vertex.arrayMat4(location, attrib -> attrib
					.pointer(p -> p.stride(VERTEX_STRIDE * 4, VERTEX_STRIDE))
					.divisor(1));
		}
	}

	/**
	 *
	 * @param buffer
	 * @param location 
	 */
	public record BoneID(IntBuffer buffer, int location) implements Vertex {
		/**
		 * @param data
		 * @param location
		 * @return a {@link BoneID} wrapping the given data at vertex attribute location.
		 */
		public static BoneID wrap(int[] data, int location) {
			return new BoneID(memAllocInt(data.length).put(data).flip(), location);
		}

		public void attribute(VertexBuilder builder) {
			builder.array(location, attrib -> attrib.iPointer(_ -> {}));
		}
	}
}
