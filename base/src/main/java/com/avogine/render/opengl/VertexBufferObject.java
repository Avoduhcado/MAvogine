package com.avogine.render.opengl;

import static org.lwjgl.opengl.GL15.*;

import java.nio.*;
import java.util.function.Consumer;

/**
 * @param id the buffer object name.
 * @param target the target to which the buffer object is bound.
 * @param usage the expected usage pattern of the data store.
 */
public record VertexBufferObject(int id, int target, int usage) {
	private static final Consumer<VertexBufferObject> BIND = VertexBufferObject::bind;
	private static final Consumer<VertexBufferObject> UNBIND = VertexBufferObject::unbind;
	
	/**
	 * @param target the target to which the buffer object is bound.
	 * @param usage the expected usage pattern of the data store.
	 */
	public VertexBufferObject(int target, int usage) {
		this(glGenBuffers(), target, usage);
	}
	
	/**
	 * @param <T> 
	 * @param target
	 * @param dataBuffer 
	 * @param usage
	 */
	public <T extends Buffer> VertexBufferObject(int target, DataBuffer<T> dataBuffer, int usage) {
		this(target, usage);
		bind(vbo -> vbo.bufferData(dataBuffer));
	}
	
	/**
	 * Bind this buffer object to its target.
	 */
	public void bind() {
		glBindBuffer(target, id);
	}
	
	/**
	 * Un-bind this buffer object target.
	 */
	public void unbind() {
		glBindBuffer(target, 0);
	}

	/**
	 * @param boundVBO
	 */
	public void bind(Consumer<VertexBufferObject> boundVBO) {
		BIND.andThen(boundVBO).andThen(UNBIND).accept(this);
	}
	
	/**
	 * Delete this buffer object.
	 */
	public void cleanup() {
		glDeleteBuffers(id);
	}
	
	/**
	 * @param <T>
	 * @param size the size in bytes of the buffer object's new data store.
	 * @param data the data to copy into the buffer's data store, or null.
	 */
	public <T extends Buffer> void bufferData(long size, T data) {
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
	
	private <T extends Buffer> void bufferData(DataBuffer<T> dataBuffer) {
		bufferData(dataBuffer.size, dataBuffer.buffer);
	}
	
	/**
	 * @param size the size in bytes of the buffer object's new data store.
	 */
	public void bufferData(long size) {
		bufferData(size, null);
	}
	
	/**
	 * @param <T>
	 * @param data the data to copy into the buffer's data store, or null.
	 */
	public <T extends Buffer> void bufferData(T data) {
		bufferData(0, data);
	}
	
	/**
	 * @param <T>
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
	 * @param data the data to copy into the buffer's data store, or null.
	 */
	public <T extends Buffer> void bufferSubData(T data) {
		bufferSubData(0, data);
	}

	/**
	 *
	 * @param <T>
	 * @param buffer
	 * @param size the size in bytes of the buffer object's new data store.
	 */
	public record DataBuffer<T extends Buffer>(T buffer, long size) {
		/**
		 * @param buffer
		 */
		public DataBuffer(T buffer) {
			this(buffer, getBufferSizeInBytes(buffer));
		}
		
		/**
		 * @param size the size in bytes of the buffer object's new data store.
		 */
		public DataBuffer(long size) {
			this(null, size);
		}
		
		/**
		 * 
		 */
		public DataBuffer {
			if (buffer == null && size < 0) {
				throw new IllegalArgumentException("If buffer is null, size must be greater than or equal to 0.");
			} else if (buffer != null && size != getBufferSizeInBytes(buffer)) {
				throw new IllegalArgumentException("If buffer is non-null, size must be equal to buffer limit.");
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
	
	/**
	 * @param <SELF> 
	 * XXX This class should be sealed since all allowed sub-types are known, but Eclipse 4.39.0 can't seem to handle sealed, inner sub-types with complex generics.
	 */
	@SuppressWarnings("java:S119")
	public abstract static class Builder<SELF extends Builder<SELF>> {
		protected final int target;
		protected DataStore dataStore = new DataStore();
		
		protected Builder(int target) {
			this.target = target;
		}
		
		protected abstract SELF self();
		
		protected VertexBufferObject build() {
			return new VertexBufferObject(target, dataStore.dataBuffer, dataStore.usage);
		}
		
		/**
		 * @param <T>
		 * @param buffer a pointer to data that will be copied into the data store for initialization, or NULL if no data is to be copied.
		 * @return DataStore
		 */
		public <T extends Buffer> DataStoreBuilder data(T buffer) {
			return new DataStoreBuilder(buffer);
		}
		
		/**
		 * @param size the size in bytes of the buffer object's new data store.
		 * @return DataStore
		 */
		public DataStoreBuilder data(long size) {
			return new DataStoreBuilder(size);
		}
		
		/**
		 * @param <T>
		 * @param buffer a pointer to data that will be copied into the data store for initialization, or NULL if no data is to be copied.
		 * @return this
		 */
		public <T extends Buffer> SELF bufferData(T buffer) {
			return data(buffer).create();
		}

		/**
		 * @param size the size in bytes of the buffer object's new data store.
		 * @return this
		 */
		public SELF bufferData(long size) {
			return data(size).create();
		}
		
		/**
		 *
		 * @param dataBuffer
		 * @param usage
		 */
		public record DataStore(DataBuffer<? extends Buffer> dataBuffer, int usage) {
			/**
			 * 
			 */
			public DataStore() {
				this(null, GL_STATIC_DRAW);
			}
		}
		
		/**
		 *
		 */
		public abstract class UsageFrequencyStep extends UsageNatureStep {
			
			protected abstract UsageNatureStep frequency(int frequency);
			
			/**
			 * The data store contents will be modified once and used at most a few times.
			 * @return BufferUsageType
			 */
			public UsageNatureStep glStream() {
				return frequency(GL_STREAM_DRAW);
			}
			
			/**
			 * The data store contents will be modified once and used many times.
			 * @return BufferUsageType
			 */
			public UsageNatureStep glStatic() {
				return frequency(GL_STATIC_DRAW);
			}
			
			/**
			 * The data store contents will be modified repeatedly and used many times.
			 * @return BufferUsageType
			 */
			public UsageNatureStep glDynamic() {
				return frequency(GL_DYNAMIC_DRAW);
			}
		}
		
		/**
		 *
		 */
		public abstract class UsageNatureStep {
			
			protected abstract SELF nature(int nature);
			
			/**
			 * The data store contents are modified by the application, and used as the source for GL drawing and image specification commands.
			 * @return CreateStep
			 */
			public SELF draw() {
				return nature(0);
			}
			
			/**
			 * The data store contents are modified by reading data from the GL, and used to return that data when queried by the application.
			 * @return CreateStep
			 */
			public SELF read() {
				return nature(1);
			}
			
			/**
			 * The data store contents are modified by reading data from the GL, and used as the source for GL drawing and image specification commands.
			 * @return CreateStep
			 */
			public SELF copy() {
				return nature(2);
			}
		}
		
		/**
		 *
		 */
		public class DataStoreBuilder extends UsageFrequencyStep {
			private final DataBuffer<? extends Buffer> dataBuffer;
			private int usageFrequency = GL_STATIC_DRAW;
			private int usageNature;
			
			private <T extends Buffer> DataStoreBuilder(T data) {
				dataBuffer = new DataBuffer<>(data);
			}
			
			private DataStoreBuilder(long dataSize) {
				dataBuffer = new DataBuffer<>(dataSize);
			}
			
			private DataStore build() {
				return new DataStore(dataBuffer, getUsage());
			}
			
			private SELF create() {
				dataStore = build();
				return self();
			}
			
			@Override
			protected UsageNatureStep frequency(int frequency) {
				this.usageFrequency = frequency;
				return this;
			}
			
			@Override
			protected SELF nature(int nature) {
				this.usageNature = nature;
				return create();
			}
			
			private int getUsage() {
				return usageFrequency + usageNature;
			}
		}
	}
}
