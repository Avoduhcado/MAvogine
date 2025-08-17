package com.avogine.render.opengl;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

import java.util.*;

import org.lwjgl.opengl.GL20;

/**
 * 
 */
public abstract class VertexArrayObject {
	
	private final int id;
	private final VertexBufferObject[] vertexBufferObjects;
	private final int vertexCount;
	
	protected VertexArrayObject(int id, VertexBufferObject[] vertexBufferObjects, int vertexCount) {
		this.id = id;
		this.vertexBufferObjects = vertexBufferObjects;
		this.vertexCount = vertexCount;
	}
	
	protected VertexArrayObject(Builder builder, int vertexCount) {
		this(builder.id(), builder.vertexBufferObjects().toArray(VertexBufferObject[]::new), vertexCount);
	}
	
	protected VertexArrayObject(Builder builder) {
		this(builder, 0);
	}
	
	/**
	 * Set the currently bound vertex array to 0.
	 */
	public static void unbind() {
		glBindVertexArray(0);
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		Arrays.stream(vertexBufferObjects).forEach(VertexBufferObject::cleanup);
		glDeleteVertexArrays(id);
	}
	
	/**
	 * Bind this vertex array.
	 */
	public void bind() {
		glBindVertexArray(id);
	}
	
	/**
	 * Render this vertex array.
	 */
	public abstract void draw();
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @return the vertexBufferObjects
	 */
	public VertexBufferObject[] getVertexBufferObjects() {
		return vertexBufferObjects;
	}
	
	/**
	 * @return the number of vertices.
	 */
	public int getVertexCount() {
		return vertexCount;
	}
	
	/**
	 * @param id
	 * @param vertexBufferObjects
	 */
	public static record Builder(int id, List<VertexBufferObject> vertexBufferObjects) implements AutoCloseable {
		
		/**
		 * 
		 * @param id
		 * @param vertexBufferObjects
		 */
		public Builder {
			glBindVertexArray(id);
		}
		
		/**
		 * Convenience constructor to build and bind a new vertex array builder.
		 */
		public Builder() {
			this(glGenVertexArrays(), new ArrayList<>());
		}
		
		/**
		 * @param vertexBufferObject
		 * @return this
		 */
		public Builder buffer(VertexBufferObject vertexBufferObject) {
			vertexBufferObjects.add(vertexBufferObject);
			return this;
		}

		/**
		 * @param vertexAttrib
		 * @return this
		 */
		public Builder attrib(VertexAttrib vertexAttrib) {
			vertexAttrib.enable();
			return this;
		}
		
		@Override
		public void close() {
			glBindVertexArray(0);
		}
	}
	
	/**
	 * A wrapper class for OpenGL's vertex formats.
	 * </br>
	 * The intended usage is to construct and configure vertex attributes through the builder pattern. This record exposes
	 * a static creation method to enable a {@link VertexAttrib} targeting a specific array index and then apply
	 * necessary attribute configurations.
	 * </br>
	 * {@snippet :
	 * VertexAttrib2.array(0).pointer(Format.initial())
	 * }
	 * 
	 * @param index the index of the generic vertex attribute to be enabled.
	 * @see <a href="https://www.khronos.org/opengl/wiki/Vertex_Specification">Vertex Specification</a>
	 */
	public static record VertexAttrib(int index) {
		
		/**
		 * Construct a new {@link VertexAttrib} for the given array index of the currently bound {@link VertexArrayObject}.
		 * @param index the index of the generic vertex attribute.
		 * @return a new {@link VertexAttrib} for the given array index of the currently bound {@link VertexArrayObject}.
		 */
		public static VertexAttrib array(int index) {
			return new VertexAttrib(index);
		}
		
		/**
		 * 
		 * @param size
		 * @param type
		 * @param normalized
		 * @param stride
		 * @param pointer
		 * @return this
		 */
		public VertexAttrib pointer(int size, int type, boolean normalized, int stride, long pointer) {
			glVertexAttribPointer(index, size, type, normalized, stride, pointer);
			return this;
		}
		
		/**
		 * @param format
		 * @return this
		 */
		public VertexAttrib pointer(Format format) {
			return switch (format) {
				case Format(var size, var type, var normalized, var stride, var pointer) -> pointer(size, type, normalized, stride, pointer);
			};
		}
		
		/**
		 * @param divisor
		 * @return this
		 */
		public VertexAttrib divisor(int divisor) {
			glVertexAttribDivisor(index, divisor);
			return this;
		}
		
		/**
		 * @return this
		 */
		public VertexAttrib enable() {
			glEnableVertexAttribArray(index);
			return this;
		}
		
		/**
		 *
		 * @param size
		 * @param type
		 * @param normalized
		 * @param stride
		 * @param pointer
		 */
		public static record Format(int size, int type, boolean normalized, int stride, long pointer) {
			/**
			 * @return a default component {@link Format} for a vertex attribute set to OpenGL's initial values for {@link GL20#glVertexAttribPointer}
			 */
			public static Format initial() {
				return new Format(4, GL_FLOAT, false, 0, 0);
			}
			
			/**
			 * @param size the number of values per vertex that are stored in the array. The initial value is 4. One of: 1	2	3	4
			 * @return a new {@link Format} for un-normalized {@code float} values that are considered to be tightly packed in the buffer with specified size.
			 */
			public static Format tightlyPackedUnnormalizedFloat(int size) {
				return new Format(size, GL_FLOAT, false, 0, 0);
			}
		}
	}
}
