package com.avogine.render.opengl;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

import java.nio.*;
import java.util.*;

import org.lwjgl.opengl.GL20;

import com.avogine.render.model.mesh.VertexArrayData;

/**
 * @param <T> The type of {@link VertexArrayData} to construct this Vertex Array Object from.
 */
public abstract class VertexArrayObject<T extends VertexArrayData> {
	
	private final int id;
	private final VertexBufferObject[] vertexBufferObjects;
	private final int vertexCount;
	
	protected VertexArrayObject(T vertexData) {
		vertexCount = vertexData.getVertexCount();
		var builder = init(vertexData);
		id = builder.id();
		vertexBufferObjects = builder.vertexBufferObjects().toArray(VertexBufferObject[]::new);
	}

	/**
	 * Set the currently bound vertex array to 0.
	 */
	public static void unbind() {
		glBindVertexArray(0);
	}
	
	protected abstract Builder init(T vertexData);
	
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
	
	protected static Builder initVAO() {
		return new Builder();
	}
	
	/**
	 * @param id
	 * @param vertexBufferObjects
	 */
	protected record Builder(int id, List<VertexBufferObject> vertexBufferObjects) {
		
		/**
		 * Convenience constructor to build and bind a new vertex array builder.
		 */
		public Builder() {
			this(glGenVertexArrays(), new ArrayList<>());
			glBindVertexArray(id);
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
	}
	
	/**
	 * A wrapper class of OpenGL's Vertex Buffer Object.
	 * 
	 * @param id the object ID of the vertex buffer.
	 * @param target the target to which the buffer object is bound. This target will be re-used by any subsequent bind calls.
	 * @param usage the expected usage pattern of the data store. This usage will be re-used by any subsequent bufferData calls.
	 * @see <a href="https://www.khronos.org/opengl/wiki/Buffer_Object">Buffer Object</a>
	 */
	protected record VertexBufferObject(int id, int target, int usage) {
		/**
		 * @param <T>
		 * @param data
		 * @return an array buffer with its data store filled and configured for static drawing.
		 */
		public static <T extends Buffer> VertexBufferObject arrayBufferStaticDraw(T data) {
			return new VertexBufferObject(glGenBuffers(), GL_ARRAY_BUFFER, GL_STATIC_DRAW).bind().bufferData(data);
		}
		
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
	protected record VertexAttrib(int index) {
		
		/**
		 * Construct a new {@link VertexAttrib} for the given array index of the currently bound {@link VertexArrayObject}.
		 * @param index the index of the generic vertex attribute.
		 * @return this
		 */
		public static VertexAttrib array(int index) {
			return new VertexAttrib(index);
		}
		
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
		public record Format(int size, int type, boolean normalized, int stride, long pointer) {
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
