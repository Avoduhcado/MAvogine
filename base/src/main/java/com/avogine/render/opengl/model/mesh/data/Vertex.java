package com.avogine.render.opengl.model.mesh.data;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL20C.*;
import static org.lwjgl.opengl.GL30C.glVertexAttribIPointer;
import static org.lwjgl.opengl.GL33C.glVertexAttribDivisor;

import java.nio.*;

import com.avogine.render.opengl.VBO;

/**
 *
 * @param buffer the {@link VBO} to bind for this vertex.
 * @param attribs an array of {@link VertexAttrib} to enable for this vertex.
 */
public record Vertex(VBO buffer, VertexAttrib...attribs) {
	
	/**
	 * @param <T>
	 * @param data
	 * @param attribs
	 */
	public <T extends Buffer> Vertex(T data, VertexAttrib...attribs) {
		this(VBO.arrayBuffer(data), attribs);
	}
	
	/**
	 * @param buffer
	 * @param location
	 * @return a new {@link Vertex} wrapping a {@link VBO} expected to contain tightly packed, 2D, unnormalized float values.
	 */
	public static Vertex vertex2D(VBO buffer, int location) {
		return new Vertex(buffer, new Attrib(location, Attrib.POINTER_2F));
	}
	
	/**
	 * @param <T>
	 * @param data
	 * @param location
	 * @return a new {@link Vertex} wrapping a {@link VBO} expected to contain tightly packed, 2D, unnormalized float values.
	 */
	public static <T extends Buffer> Vertex vertex2D(T data, int location) {
		return vertex2D(VBO.arrayBuffer(data), location);
	}

	/**
	 * @param buffer
	 * @param location
	 * @return a new {@link Vertex} wrapping a {@link VBO} expected to contain tightly packed, 3D, unnormalized float values.
	 */
	public static Vertex vertex3D(VBO buffer, int location) {
		return new Vertex(buffer, new Attrib(location, Attrib.POINTER_3F));
	}
	
	/**
	 * @param <T>
	 * @param data
	 * @param location
	 * @return a new {@link Vertex} wrapping a {@link VBO} expected to contain tightly packed, 3D, unnormalized float values.
	 */
	public static <T extends Buffer> Vertex vertex3D(T data, int location) {
		return vertex3D(VBO.arrayBuffer(data), location);
	}

	/**
	 * @param buffer
	 * @param location
	 * @return a new {@link Vertex} wrapping a {@link VBO} expected to contain tightly packed, 4D, unnormalized float values.
	 */
	public static Vertex vertex4D(VBO buffer, int location) {
		return new Vertex(buffer, new Attrib(location, Attrib.POINTER_4F));
	}
	
	/**
	 * @param <T>
	 * @param data
	 * @param location
	 * @return a new {@link Vertex} wrapping a {@link VBO} expected to contain tightly packed, 4D, unnormalized float values.
	 */
	public static <T extends Buffer> Vertex vertex4D(T data, int location) {
		return vertex4D(VBO.arrayBuffer(data), location);
	}

	/**
	 * @param buffer
	 * @param location
	 * @return a new {@link Vertex} wrapping a {@link VBO} expected to contain 4 sets of interleaved, 4D, unnormalized float values.
	 */
	public static Vertex vertex4x4Instanced(VBO buffer, int location) {
		return new Vertex(buffer, new AttribMat4(location, AttribMat4.POINTER_MAT4F_INTERLEAVED, 1));
	}
	
	/**
	 * @param <T>
	 * @param data
	 * @param location
	 * @return a new {@link Vertex} wrapping a {@link VBO} expected to contain 4 sets of interleaved, 4D, unnormalized float values.
	 */
	public static <T extends Buffer> Vertex vertex4x4Instanced(T data, int location) {
		return vertex4x4Instanced(VBO.arrayBuffer(data), location);
	}
	
	/**
	 * @param buffer 
	 * @param location
	 * @return a new {@link Vertex} wrapping a {@link VBO} expected to contain tightly packed, 4D integer values.
	 */
	public static Vertex boneID(VBO buffer, int location) {
		return new Vertex(buffer, new Attrib(location, Attrib.IPOINTER_4I));
	}
	
	/**
	 * @param data
	 * @param location
	 * @return a new {@link Vertex} wrapping a {@link VBO} expected to contain tightly packed, 4D integer values.
	 */
	public static Vertex boneID(IntBuffer data, int location) {
		return boneID(VBO.arrayBuffer(data), location);
	}
	
	public sealed interface VertexAttrib {
		/**
		 * 
		 */
		public void enable();
		
		/**
		 * @return {@link Pointer.Builder}
		 */
		public static Pointer.Builder pointer() {
			return new Pointer.Builder();
		}
		
		/**
		 * @return {@link IPointer.Builder}
		 */
		public static IPointer.Builder ipointer() {
			return new IPointer.Builder();
		}
		
		public sealed interface AttribPointer {}
		
		/**
		 *
		 * @param size
		 * @param type
		 * @param normalized
		 * @param stride
		 * @param pointer
		 */
		public record Pointer(int size, int type, boolean normalized, int stride, long pointer) implements AttribPointer {
			
			/**
			 *
			 */
			public static final class Builder {
				private int size = 4;
				private int type = GL_FLOAT;
				private boolean normalized;
				private int stride;
				private long pointer;
				
				/**
				 * @return a newly constructed {@link Pointer}
				 */
				public Pointer build() {
					return new Pointer(size, type, normalized, stride, pointer);
				}
				
				/**
				 * @param size
				 * @return this
				 */
				public Builder size(int size) {
					this.size = size;
					return this;
				}
				
				/**
				 * @param type
				 * @return this
				 */
				public Builder type(int type) {
					this.type = type;
					return this;
				}
				
				/**
				 * @param normalized
				 * @return this
				 */
				public Builder normalized(boolean normalized) {
					this.normalized = normalized;
					return this;
				}
				
				/**
				 * @param stride
				 * @param pointer
				 * @return this
				 */
				public Builder interleaved(int stride, long pointer) {
					this.stride = stride;
					this.pointer = pointer;
					return this;
				}
			}
		}

		/**
		 *
		 * @param size
		 * @param type
		 * @param stride
		 * @param pointer
		 */
		public record IPointer(int size, int type, int stride, long pointer) implements AttribPointer {
			
			/**
			 *
			 */
			public static final class Builder {
				private int size = 4;
				private int type = GL_INT;
				private int stride;
				private long pointer;
				
				/**
				 * @return a newly constructed {@link IPointer}
				 */
				public IPointer build() {
					return new IPointer(size, type, stride, pointer);
				}
				
				/**
				 * @param size
				 * @return this
				 */
				public Builder size(int size) {
					this.size = size;
					return this;
				}
				
				/**
				 * @param type
				 * @return this
				 */
				public Builder type(int type) {
					this.type = type;
					return this;
				}
				
				/**
				 * @param stride
				 * @param pointer
				 * @return this
				 */
				public Builder interleaved(int stride, long pointer) {
					this.stride = stride;
					this.pointer = pointer;
					return this;
				}
			}
		}
	}
	
	/**
	 *
	 * @param index
	 * @param pointer
	 * @param divisor
	 */
	public record Attrib(int index, AttribPointer pointer, int divisor) implements VertexAttrib {
		public static final Pointer POINTER_2F = new Pointer(2, GL_FLOAT, false, 0, 0);
		public static final Pointer POINTER_3F = new Pointer(3, GL_FLOAT, false, 0, 0);
		public static final Pointer POINTER_4F = new Pointer(4, GL_FLOAT, false, 0, 0);
		
		public static final IPointer IPOINTER_4I = new IPointer(4, GL_INT, 0, 0);
		
		/**
		 * @param index
		 * @param pointer
		 */
		public Attrib(int index, AttribPointer pointer) {
			this(index, pointer, 0);
		}
		
		@Override
		public void enable() {
			glEnableVertexAttribArray(index);
			switch (pointer) {
				case Pointer(var size, var type, @SuppressWarnings("preview") var normalized, var stride, @SuppressWarnings("preview") var pointer) -> glVertexAttribPointer(index, size, type, normalized, stride, pointer);
				case IPointer(var size, var type, var stride, @SuppressWarnings("preview") var pointer) -> glVertexAttribIPointer(index, size, type, stride, pointer);
				case null -> { /* No pointer to set. */ }
			}
			glVertexAttribDivisor(index, divisor);
		}
	}
	
	/**
	 *
	 * @param index
	 * @param pointer
	 * @param divisor
	 */
	public record AttribMat4(int index, AttribPointer pointer, int divisor) implements VertexAttrib {
		private static final int VERTEX_STRIDE = Float.BYTES * 4;
		
		public static final Pointer POINTER_MAT4F_INTERLEAVED = new Pointer(4, GL_FLOAT, false, VERTEX_STRIDE * 4, VERTEX_STRIDE);
		
		/**
		 * @param index
		 * @param pointer
		 */
		public AttribMat4(int index, AttribPointer pointer) {
			this(index, pointer, 0);
		}
		
		@Override
		public void enable() {
			for (int i = 0; i < 4; i++) {
				glEnableVertexAttribArray(index + i);
				switch (pointer) {
					case Pointer(var size, var type, @SuppressWarnings("preview") var normalized, var stride, @SuppressWarnings("preview") var pointer) -> glVertexAttribPointer(index + i, size, type, normalized, stride, i * pointer);
					case IPointer(var size, var type, var stride, @SuppressWarnings("preview") var pointer) -> glVertexAttribIPointer(index + i, size, type, stride, i * pointer);
					case null -> { /* No pointer to set. */ }
				}
				glVertexAttribDivisor(index + i, divisor);
			}
		}
		
	}
	
}
