package com.avogine.render.opengl;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

import java.nio.IntBuffer;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.*;

import org.lwjgl.opengl.*;

import com.avogine.render.opengl.VAO.Builder.VertexAttrib;
import com.avogine.render.opengl.VBO.VertexArrayBuilder;

/**
 * @param id 
 * @param vertexBufferObjects 
 */
public record VAO(int id, VBO[] vertexBufferObjects) {
	private static final Consumer<VBO> BIND_BUFFER = VBO::bind;
	
	private VAO(Builder builder) {
		this(glGenVertexArrays(), new VBO[builder.vertexArray.size()]);
		
		bind();
		
		int index = 0;
		for (var entry : builder.vertexArray.entrySet()) {
			vertexBufferObjects[index] = INIT_BUFFER_AND_VERTEX_ATTRIB.apply(entry);
			index++;
		}
		// XXX Some sort of special handling for ELEMENT_ARRAY_BUFFER?
		
		unbind();
	}
	
	/**
	 * @param configuration
	 * @return a newly constructed {@link VAO} with the configurations from {@code configuration} applied.
	 */
	public static VAO gen(UnaryOperator<Builder> configuration) {
		return configuration.andThen(Builder.TO_VAO).apply(new Builder());
	}
	
	/**
	 * Convenience method to clear the currently bound vertex array.
	 */
	public static void unbind() {
		glBindVertexArray(0);
	}
	
	/**
	 * Cleanup all vertex buffers and then delete this vertex array.
	 */
	public void cleanup() {
		Arrays.stream(vertexBufferObjects).forEach(VBO::cleanup);
		glDeleteVertexArrays(id);
	}
	
	/**
	 * Bind this vertex array.
	 */
	public void bind() {
		glBindVertexArray(id);
	}
	
	/**
	 * @param vboIndex
	 * @param boundBuffer
	 */
	public void bindBuffer(int vboIndex, Consumer<VBO> boundBuffer) {
		BIND_BUFFER
			.andThen(boundBuffer)
			.andThen(_ -> VBO.unbind())
			.accept(vertexBufferObjects[vboIndex]);
	}
	
	/**
	 * @param <E>
	 * @param vertexArrayEnum
	 * @param boundBuffer
	 */
	public <E extends Enum<E>> void bindBuffer(E vertexArrayEnum, Consumer<VBO> boundBuffer) {
		bindBuffer(vertexArrayEnum.ordinal(), boundBuffer);
	}
	
	private static final Function<Entry<VertexArrayBuilder, SequencedSet<VertexAttrib>>, VBO> INIT_BUFFER_AND_VERTEX_ATTRIB = entry -> {
		VBO vbo = entry.getKey().build();
		
		for (var vertexAttrib : entry.getValue()) {
			int index = vertexAttrib.index;
			glEnableVertexAttribArray(index);
			if (vertexAttrib.pointer instanceof VertexAttrib.Pointer (var size, var type, var normalized, var stride, var pointer)) {
				glVertexAttribPointer(index, size, type, normalized, stride, pointer);
			}
			glVertexAttribDivisor(index, vertexAttrib.divisor);
		}
		
		return vbo;
	};
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(vertexBufferObjects);
		result = prime * result + Objects.hash(id);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof VAO))
			return false;
		VAO other = (VAO) obj;
		return id == other.id && Arrays.equals(vertexBufferObjects, other.vertexBufferObjects);
	}

	@Override
	public String toString() {
		return "VAO [id=" + id + ", vertexBufferObjects=" + Arrays.toString(vertexBufferObjects) + "]";
	}
	
	/**
	 *
	 */
	public static final class Builder {
		private static final Function<Builder, VAO> TO_VAO = Builder::build;
		
		private final SequencedMap<VertexArrayBuilder, SequencedSet<VertexAttrib>> vertexArray;
		
		/**
		 * Instances of this class should only be constructed via the VAO static gen method.
		 */
		private Builder() {
			vertexArray = new LinkedHashMap<>();
		}
		
		/**
		 * Initialize the vertex array object and return the newly constructed VAO.
		 * @return a newly constructed VAO.
		 */
		public VAO build() {
			return new VAO(this);
		}
		
		/**
		 * @param vertexBuffer
		 * @return this
		 */
		Builder bind(VertexArrayBuilder vertexBuffer) {
			vertexArray.putLast(vertexBuffer, new LinkedHashSet<>());
			return this;
		}
		
		/**
		 * @param target 
		 * @return a VertexBuffer
		 */
		public VertexArrayBuilder buffer(int target) {
			return new VertexArrayBuilder(target, this);
		}
		
		/**
		 * Add an ARRAY_BUFFER pre-configured for STATIC_DRAW usage.
		 * @return a VertexBuffer
		 */
		public VertexArrayBuilder buffer() {
			return buffer(GL_ARRAY_BUFFER).staticUsage().draw();
		}
		
		/**
		 * Add an ELEMENT_ARRAY_BUFFER pre-configured for STATIC_DRAW usage.
		 * @return a VertexBuffer
		 */
		public VertexArrayBuilder elementBuffer() {
			return buffer(GL_ELEMENT_ARRAY_BUFFER).staticUsage().draw();
		}
		
		/**
		 * @param indices
		 * @return a VertexBuffer
		 */
		public Builder elementBuffer(IntBuffer indices) {
			return bind(elementBuffer().data(indices));
		}
		
		/**
		 * @param index
		 * @return a VertexAttrib
		 */
		public VertexAttrib vertexAttribArray(int index) {
			return new VertexAttrib(index);
		}
		
		/**
		 * @param <E>
		 * @param vertexArrayEnum
		 * @return a VertexAttrib
		 */
		public <E extends Enum<E>> VertexAttrib vertexAttribArray(E vertexArrayEnum) {
			return vertexAttribArray(vertexArrayEnum.ordinal());
		}
		
		/**
		 * A wrapper class for OpenGL's vertex attributes.
		 * </p>
		 * The intended usage is to construct and configure vertex attributes through the builder pattern. This class exposes
		 * a static creation method to enable a {@link VertexAttrib} targeting a specific attribute location and organize data in
		 * the vertex attribute array.
		 * </p>
		 * {@snippet :
		 * vaoBuilder.vertexAttribArray(0)
		 * 	.pointer().size(3).tightlyPacked()
		 * 	.enable()
		 * }
		 * 
		 * @see <a href="https://www.khronos.org/opengl/wiki/Vertex_Specification">Vertex Specification</a>
		 */
		public class VertexAttrib {
			private final int index;
			private Pointer pointer;
			private int divisor;
			
			private VertexAttrib(int index) {
				this.index = index;
			}
			
			/**
			 * Enable this vertex attribute array for the current vertex buffer.
			 * @return Builder
			 */
			public Builder enable() {
				vertexArray.lastEntry().getValue().add(this);
				return Builder.this;
			}
			
			/**
			 * @param size the number of values per vertex that are stored in the array.
			 * @param type the data type of each component in the array. The initial value is GL_FLOAT.
			 * @param normalized whether fixed-point data values should be normalized or converted directly as fixed-point values when they are accessed.
			 * @param stride the byte offset between consecutive generic vertex attributes. If stride is 0, the generic vertex attributes are understood to be tightly packed in 
			 * the array. The initial value is 0.
			 * @param pointer the vertex attribute data or the offset of the first component of the first generic vertex attribute in the array in the data store of the buffer 
			 * currently bound to the {@link GL15#GL_ARRAY_BUFFER ARRAY_BUFFER} target. The initial value is 0.
			 * @return this
			 */
			public VertexAttrib pointer(int size, int type, boolean normalized, int stride, long pointer) {
				this.pointer = new Pointer(size, type, normalized, stride, pointer);
				return this;
			}
			
			/**
			 * @return a Pointer
			 */
			public PointerBuilder pointer() {
				return new PointerBuilder();
			}
			
			/**
			 *
			 */
			public interface SizeStep extends TypeStep {
				/**
				 * @param size the number of values per vertex that are stored in the array. The initial value is 4. One of: 1	2	3	4 {@link GL12#GL_BGRA BGRA}
				 * @return TypeStep
				 */
				public TypeStep size(int size);
				
				/**
				 * @return TypeStep
				 */
				public default TypeStep b() {
					return size(1);
				}

				/**
				 * @return TypeStep
				 */
				public default TypeStep bg() {
					return size(2);
				}

				/**
				 * @return TypeStep
				 */
				public default TypeStep bgr() {
					return size(3);
				}
			}
			
			/**
			 *
			 */
			public interface TypeStep extends NormalizedStep {
				/**
				 * @param type
				 * @return NormalizedStep
				 */
				public NormalizedStep type(int type);
				
				/**
				 * @return NormalizedStep
				 */
				public default NormalizedStep floats() {
					return type(GL_FLOAT);
				}
				
				/**
				 * @return NormalizedStep
				 */
				public default NormalizedStep unsignedByte() {
					return type(GL_UNSIGNED_BYTE);
				}
			}
			
			/**
			 *
			 */
			public interface NormalizedStep extends OffsetStep {
				/**
				 * @return OffsetStep
				 */
				public OffsetStep normalized();
			}
			
			/**
			 *
			 */
			public interface OffsetStep {
				/**
				 * @param stride
				 * @param pointer
				 * @return VertexAttrib
				 */
				public VertexAttrib interleaved(int stride, long pointer);
				
				/**
				 * @return VertexAttrib
				 */
				public default VertexAttrib tightlyPacked() {
					return interleaved(0, 0);
				}
			}
			
			/**
			 *
			 */
			public final class PointerBuilder implements SizeStep {
				private int size = 4;
				private int type = GL_FLOAT;
				private boolean normalized;
				private int stride;
				private long pointer;
				
				private VertexAttrib specify() {
					return VertexAttrib.this.pointer(size, type, normalized, stride, pointer);
				}
				
				@Override
				public TypeStep size(int size) {
					this.size = size;
					return this;
				}
				
				@Override
				public NormalizedStep type(int type) {
					this.type = type;
					return this;
				}
				
				@Override
				public OffsetStep normalized() {
					normalized = true;
					return this;
				}
				
				@Override
				public VertexAttrib interleaved(int stride, long pointer) {
					this.stride = stride;
					this.pointer = pointer;
					return specify();
				}
			}
			
			/**
			 * @param size the number of values per vertex that are stored in the array.
			 * @param type the data type of each component in the array. The initial value is GL_FLOAT.
			 * @param normalized whether fixed-point data values should be normalized or converted directly as fixed-point values when they are accessed.
			 * @param stride the byte offset between consecutive generic vertex attributes. If stride is 0, the generic vertex attributes are understood to be tightly packed in 
			 * the array. The initial value is 0.
			 * @param pointer the vertex attribute data or the offset of the first component of the first generic vertex attribute in the array in the data store of the buffer 
			 * currently bound to the {@link GL15#GL_ARRAY_BUFFER ARRAY_BUFFER} target. The initial value is 0.
			 */
			public record Pointer(int size, int type, boolean normalized, int stride, long pointer) {}
			
			/**
			 * @param divisor
			 * @return Builder
			 */
			public VertexAttrib divisor(int divisor) {
				this.divisor = divisor;
				return this;
			}
		}
		
	}

}
