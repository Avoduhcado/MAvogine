package com.avogine.render.opengl;

import static org.lwjgl.opengl.GL15.*;

import java.nio.*;
import java.util.function.Function;

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
	
	private VBO(Builder<?> builder) {
		this(builder.target, builder.usage);
		bind();
		if (builder.bufferData != null) {
			switch (builder.bufferData) {
				case Builder.SizeBuffer(var size) -> bufferData(size);
				case Builder.DataBuffer(var data) -> bufferData(data);
			}
		}
	}
	
	/**
	 * @param usage the expected usage pattern of the data store.
	 * @return an array buffer configured for the given usage.
	 */
	public static VBO arrayBuffer(int usage) {
		return new VBO(GL_ARRAY_BUFFER, usage);
	}
	
	/**
	 * @return an array buffer configured for static drawing.
	 */
	public static VBO staticArray() {
		return arrayBuffer(GL_STATIC_DRAW);
	}
	
	/**
	 * @return an element array buffer configured for static drawing.
	 */
	public static VBO staticElementArray() {
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
	
	/**
	 * 
	 */
	public abstract static sealed class Builder<T extends Builder<T>> {
		static final Function<Builder<?>, VBO> TO_VBO = Builder::build;
		
		private final int target;
		private int usage;
		private BufferData bufferData;
		
		Builder(int target) {
			this.target = target;
			usage = GL_STATIC_DRAW;
		}
		
		protected abstract T self();
		
		/**
		 * @return a newly constructed VBO
		 */
		public VBO build() {
			return new VBO(this);
		}
		
		/**
		 * @param usage
		 * @return this
		 */
		public T usage(int usage) {
			this.usage = usage;
			return self();
		}
		
		/**
		 * @return a StreamUsage
		 */
		public StreamUsage stream() {
			return new StreamUsage();
		}
		
		/**
		 * @return a StaticUsage
		 */
		public StaticUsage staticUsage() {
			return new StaticUsage();
		}
		
		/**
		 * @return a DynamicUsage
		 */
		public DynamicUsage dynamic() {
			return new DynamicUsage();
		}
		
		public abstract sealed class Usage {
			/**
			 * @return VertexBuffer
			 */
			public abstract T draw();
			
			/**
			 * @return VertexBuffer
			 */
			public abstract T read();
			
			/**
			 * @return VertexBuffer
			 */
			public abstract T copy();
		}
		
		/**
		 *
		 */
		public final class StreamUsage extends Usage {
			@Override
			public T draw() {
				return usage(GL_STREAM_DRAW);
			}

			@Override
			public T read() {
				return usage(GL_STREAM_READ);
			}

			@Override
			public T copy() {
				return usage(GL_STREAM_COPY);
			}
		}
		
		/**
		 *
		 */
		public final class StaticUsage extends Usage {
			@Override
			public T draw() {
				return usage(GL_STATIC_DRAW);
			}

			@Override
			public T read() {
				return usage(GL_STATIC_READ);
			}

			@Override
			public T copy() {
				return usage(GL_STATIC_COPY);
			}
		}
		
		/**
		 *
		 */
		public final class DynamicUsage extends Usage {
			@Override
			public T draw() {
				return usage(GL_DYNAMIC_DRAW);
			}

			@Override
			public T read() {
				return usage(GL_DYNAMIC_READ);
			}

			@Override
			public T copy() {
				return usage(GL_DYNAMIC_COPY);
			}
		}
		
		/**
		 * @param size the size in bytes of the buffer object's new data store.
		 * @return this
		 */
		public T data(long size) {
			bufferData = new SizeBuffer(size);
			return self();
		}
		
		/**
		 * @param <B>
		 * @param data
		 * @return this
		 */
		public <B extends Buffer> T data(B data) {
			bufferData = new DataBuffer<>(data);
			return self();
		}
		
		public sealed interface BufferData {}
		
		/**
		 *
		 * @param size the size in bytes of the buffer object's new data store.
		 */
		public record SizeBuffer(long size) implements BufferData {}
		
		/**
		 *
		 * @param <T>
		 * @param data
		 */
		public record DataBuffer<T extends Buffer>(T data) implements BufferData {}
	}
	
	/**
	 *
	 */
	public static final class VertexArrayBuilder extends Builder<VertexArrayBuilder> {
		private final VAO.Builder arrayBuilder;
		
		VertexArrayBuilder(int target, VAO.Builder parent) {
			super(target);
			this.arrayBuilder = parent;
		}
		
		@Override
		protected VertexArrayBuilder self() {
			return this;
		}
		
		/**
		 * @return Builder
		 */
		public VAO.Builder bind() {
			return arrayBuilder.bind(this);
		}
	}
}
