package com.avogine.render.opengl;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

import java.util.*;
import java.util.function.*;

import org.lwjgl.opengl.*;

import com.avogine.render.opengl.VertexArrayObject.Builder.Vertex.*;
import com.avogine.render.opengl.VertexArrayObject.Builder.VertexBuilder.AttribBuilder.*;

/**
 * @param id 
 * @param vertexArrayBuffers 
 * @param indexBuffer 
 */
public record VertexArrayObject(int id, VertexBufferObject[] vertexArrayBuffers, VertexBufferObject indexBuffer) {
	private static final Consumer<VertexArrayObject> BIND = VertexArrayObject::bind;
	private static final Consumer<VertexArrayObject> UNBIND = VertexArrayObject::unbind;
	
	/**
	 * @param vertexArrayBuffers
	 * @param indexBuffer
	 */
	public VertexArrayObject(VertexBufferObject[] vertexArrayBuffers, VertexBufferObject indexBuffer) {
		this(glGenVertexArrays(), vertexArrayBuffers, indexBuffer);
	}
	
	/**
	 * @param vertexArray
	 * @param index
	 */
	public VertexArrayObject(List<Builder.Vertex> vertexArray, VertexBufferObject index) {
		this(vertexArray.stream().map(it -> it.buffer).toArray(VertexBufferObject[]::new), index);
		bind(_ -> {
			vertexArray.forEach(Builder.Vertex::enableArray);
			if (Objects.nonNull(index)) {
				index.bind();
			}
		});
	}
	
	/**
	 * @param init
	 * @return a new {@link VertexArrayObject} configured by {@code init}.
	 */
	public static VertexArrayObject gen(UnaryOperator<Builder> init) {
		return init.andThen(Builder::build).apply(new Builder());
	}
	
	/**
	 * 
	 */
	public void bind() {
		glBindVertexArray(id);
	}
	
	/**
	 * 
	 */
	public void unbind() {
		glBindVertexArray(0);
	}
	
	/**
	 * @param boundVAO
	 */
	public void bind(Consumer<VertexArrayObject> boundVAO) {
		BIND.andThen(boundVAO).andThen(UNBIND).accept(this);
	}
	
	/**
	 * @param vboIndex
	 * @param boundVBO
	 */
	public void bindVBO(int vboIndex, Consumer<VertexBufferObject> boundVBO) {
		bind(vao -> vao.vertexArrayBuffers[vboIndex].bind(boundVBO));
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		glDeleteBuffers(Arrays.stream(vertexArrayBuffers).mapToInt(VertexBufferObject::id).toArray());
		if (indexBuffer != null) {
			indexBuffer.cleanup();
		}
		glDeleteVertexArrays(id);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof VertexArrayObject))
			return false;
		VertexArrayObject other = (VertexArrayObject) obj;
		return id == other.id;
	}
	
	@Override
	public String toString() {
		return "VertexArrayObject [id=" + id + "]";
	}
	
	/**
	 *
	 */
	public static final class Builder {
		private List<Vertex> vertexArray = new ArrayList<>();
		private VertexBufferObject index;
		
		private Builder() {}
		
		private VertexArrayObject build() {
			return new VertexArrayObject(vertexArray, index);
		}
		
		/**
		 * @param vertexBuffer
		 * @return {@link VertexBuilder}
		 */
		public VertexBuilder vertex(VertexBufferObject vertexBuffer) {
			return new VertexBuilder(vertexBuffer);
		}
		
		/**
		 * @param vertex
		 * @return this
		 */
		public Builder vertex(Vertex vertex) {
			if (Objects.requireNonNull(vertex).buffer.target() != GL_ARRAY_BUFFER) {
				throw new IllegalArgumentException("Vertex buffer is of invalid type: " + vertex.buffer.target());
			}
			vertexArray.add(vertex);
			return this;
		}
		
		/**
		 * @param vertexBuffer
		 * @param builder
		 * @return this
		 */
		public Builder vertex(VertexBufferObject vertexBuffer, Consumer<VertexBuilder> builder) {
			builder.andThen(VertexBuilder::bind).accept(vertex(vertexBuffer));
			return this;
		}
		
		/**
		 * @param vertexBufferBuilder
		 * @param builder
		 * @return this
		 */
		public Builder vertex(UnaryOperator<VertexBufferBuilder> vertexBufferBuilder, Consumer<VertexBuilder> builder) {
			builder.andThen(VertexBuilder::bind).accept(vertex(vertexBufferBuilder.andThen(VertexBufferBuilder::build).apply(new VertexBufferBuilder())));
			return this;
		}
		
		/**
		 * @param builder
		 * @return this
		 */
		public Builder vertex(Consumer<Builder> builder) {
			builder.accept(this);
			return this;
		}
		
		/**
		 *
		 * @param buffer
		 * @param attribArray
		 */
		public record Vertex(VertexBufferObject buffer, Attrib[] attribArray) {
			/**
			 * A wrapper class for OpenGL's vertex attributes.
			 * </p>
			 * The intended usage is to construct and configure vertex attributes through the builder pattern. This class exposes
			 * a static creation method to enable a {@link Vertex.Attrib} targeting a specific attribute location and organize data in
			 * the vertex attribute array.
			 * </p>
			 * {@snippet :
			 * vertexBuilder.array(0, attrib -> attrib.
			 * 	.pointer(p -> p.size(3)))
			 * }
			 * 
			 * @see <a href="https://www.khronos.org/opengl/wiki/Vertex_Specification">Vertex Specification</a>
			 */
			public sealed interface Attrib {
				/**
				 * 
				 */
				public void enable();
			}
			
			/**
			 *
			 * @param index
			 * @param pointer
			 * @param divisor
			 */
			public record AttribVec(int index, AttribPointer pointer, int divisor) implements Attrib {
				@Override
				public void enable() {
					glEnableVertexAttribArray(index);
					switch (pointer) {
						case Pointer(var size, var type, var normalized, var stride, var pointer) -> glVertexAttribPointer(index, size, type, normalized, stride, pointer);
						case IPointer(var size, var type, var stride, var pointer) -> glVertexAttribIPointer(index, size, type, stride, pointer);
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
			public record AttribMat4(int index, AttribPointer pointer, int divisor) implements Attrib {
				@Override
				public void enable() {
					for (int i = 0; i < 4; i++) {
						glEnableVertexAttribArray(index + i);
						switch (pointer) {
							case Pointer(var size, var type, var normalized, var stride, var pointer) -> glVertexAttribPointer(index + i, size, type, normalized, stride, i * pointer);
							case IPointer(var size, var type, var stride, var pointer) -> glVertexAttribIPointer(index + i, size, type, stride, i * pointer);
							case null -> { /* No pointer to set. */ }
						}
						glVertexAttribDivisor(index + i, divisor);
					}
				}
			}
			
			/**
			 * 
			 */
			public void enableArray() {
				buffer.bind();
				for (var attrib : attribArray) {
					attrib.enable();
				}
			}
			
			@Override
			public int hashCode() {
				return Objects.hash(buffer);
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (!(obj instanceof Vertex))
					return false;
				Vertex other = (Vertex) obj;
				return Objects.equals(buffer, other.buffer);
			}

			@Override
			public String toString() {
				return "Vertex [buffer=" + buffer + ", attribArray=" + Arrays.toString(attribArray) + "]";
			}
		}
		
		/**
		 *
		 */
		public class VertexBuilder {
			private final VertexBufferObject vertexBuffer;
			private final List<Attrib> arrayAttribs;
			
			private VertexBuilder(VertexBufferObject vertexBuffer) {
				this.vertexBuffer = vertexBuffer;
				arrayAttribs = new ArrayList<>();
			}
			
			private Vertex build() {
				return new Vertex(vertexBuffer, arrayAttribs.toArray(Attrib[]::new));
			}
			
			private AttribVecBuilder array(int index) {
				return new AttribVecBuilder(index);
			}
			
			private AttribMat4Builder arrayMat4(int index) {
				return new AttribMat4Builder(index);
			}
			
			/**
			 * @param index
			 * @param attribBuilder
			 * @return this
			 */
			public VertexBuilder array(int index, Consumer<AttribVecBuilder> attribBuilder) {
				attribBuilder.andThen(AttribBuilder::enable).accept(array(index));
				return this;
			}
			
			/**
			 * @param index
			 * @param attribBuilder
			 * @return this
			 */
			public VertexBuilder arrayMat4(int index, Consumer<AttribMat4Builder> attribBuilder) {
				attribBuilder.andThen(AttribBuilder::enable).accept(arrayMat4(index));
				return this;
			}
			
			private Builder bind() {
				vertexArray.add(build());
				return Builder.this;
			}
			
			public abstract sealed class AttribBuilder<T extends Vertex.Attrib> {
				protected final int index;
				protected AttribPointer pointer;
				protected int divisor;
				
				protected AttribBuilder(int index) {
					this.index = index;
				}
				
				private VertexBuilder enableAttribArray(Attrib attrib) {
					arrayAttribs.add(attrib);
					return VertexBuilder.this;
				}
				
				protected abstract T build();
				
				private IPointerBuilder iPointer() {
					return new IPointerBuilder();
				}
				
				private PointerBuilder pointer() {
					return new PointerBuilder();
				}
				
				/**
				 * @param builder
				 * @return this
				 */
				public AttribBuilder<T> iPointer(Consumer<IPointerBuilder> builder) {
					builder.andThen(SpecifyStep::specify).accept(iPointer());
					return this;
				}
				
				/**
				 * @return this
				 */
				public AttribBuilder<T> iPointer4i() {
					return iPointer(_ -> {});
				}
				
				/**
				 * @param builder
				 * @return this
				 */
				public AttribBuilder<T> pointer(Consumer<PointerBuilder> builder) {
					builder.andThen(SpecifyStep::specify).accept(pointer());
					return this;
				}
				
				/**
				 * @return this
				 */
				public AttribBuilder<T> pointer4f() {
					return pointer(_ -> {});
				}
				
				/**
				 *
				 */
				public interface SizeIStep extends ITypeStep {
					/**
					 * @param size the number of values per vertex that are stored in the array. The initial value is 4. One of: 1	2	3	4 {@link GL12#GL_BGRA BGRA}
					 * @return {@link ITypeStep}
					 */
					public ITypeStep size(int size);
				}
				
				/**
				 *
				 */
				public interface SizeStep extends SizeIStep, TypeStep {
					@Override
					public TypeStep size(int size);
				}
				
				/**
				 *
				 */
				public interface ITypeStep extends StrideStep {
					/**
					 * @param type
					 * @return {@link StrideStep}
					 */
					public StrideStep type(int type);
					
					/**
					 * @return {@link StrideStep}
					 */
					public default StrideStep b() {
						return type(GL_BYTE);
					}
					
					/**
					 * @return {@link StrideStep}
					 */
					public default StrideStep ub() {
						return type(GL_UNSIGNED_BYTE);
					}
					
					/**
					 * @return {@link StrideStep}
					 */
					public default StrideStep i() {
						return type(GL_INT);
					}
				}
				
				/**
				 *
				 */
				public interface TypeStep extends ITypeStep, NormalizedStep {
					/**
					 * @param type
					 * @return {@link NormalizedStep}
					 */
					@Override
					public NormalizedStep type(int type);

					@Override
					public default NormalizedStep b() {
						return type(GL_BYTE);
					}

					@Override
					public default NormalizedStep ub() {
						return type(GL_UNSIGNED_BYTE);
					}

					@Override
					public default NormalizedStep i() {
						return type(GL_INT);
					}
					
					/**
					 * @return {@link NormalizedStep}
					 */
					public default NormalizedStep f() {
						return type(GL_FLOAT);
					}
				}
				
				/**
				 *
				 */
				public interface NormalizedStep extends StrideStep {
					/**
					 * @param normalized 
					 * @return {@link StrideStep}
					 */
					public StrideStep normalized(boolean normalized);
					
					/**
					 * @return {@link StrideStep}
					 */
					public default StrideStep normalized() {
						return normalized(true);
					}
				}

				/**
				 *
				 */
				public interface StrideStep extends SpecifyStep {
					/**
					 * @param stride
					 * @param pointer
					 * @return {@link SpecifyStep}
					 */
					public SpecifyStep stride(int stride, long pointer);
					
					/**
					 * @return {@link SpecifyStep}
					 */
					public default SpecifyStep packed() {
						return stride(0, 0L);
					}
				}
				
				/**
				 *
				 */
				public interface SpecifyStep {
					/**
					 * @return {@link AttribBuilder}
					 */
					public AttribBuilder<? extends Attrib> specify();
				}
				
				public sealed interface AttribPointer {}
				
				/**
				 * @param size the number of values per vertex that are stored in the array.
				 * @param type the data type of each component in the array. The initial value is GL_INT.
				 * @param stride the byte offset between consecutive generic vertex attributes. If stride is 0, the generic vertex attributes are understood to be tightly packed in 
				 * the array. The initial value is 0.
				 * @param pointer the vertex attribute data or the offset of the first component of the first generic vertex attribute in the array in the data store of the buffer 
				 * currently bound to the {@link GL15#GL_ARRAY_BUFFER ARRAY_BUFFER} target. The initial value is 0.
				 */
				public record IPointer(int size, int type, int stride, long pointer) implements AttribPointer {}
				
				/**
				 *
				 */
				public class IPointerBuilder implements SizeIStep {
					private int size = 4;
					private int type = GL_INT;
					private int stride;
					private long pointer;

					@Override
					public ITypeStep size(int size) {
						this.size = size;
						return this;
					}

					@Override
					public StrideStep type(int type) {
						this.type = type;
						return this;
					}

					@Override
					public SpecifyStep stride(int stride, long pointer) {
						this.stride = stride;
						this.pointer = pointer;
						return this;
					}

					@Override
					public AttribBuilder<?> specify() {
						AttribBuilder.this.pointer = new IPointer(size, type, stride, pointer);
						return AttribBuilder.this;
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
				public record Pointer(int size, int type, boolean normalized, int stride, long pointer) implements AttribPointer {}
				
				/**
				 *
				 */
				public class PointerBuilder implements SizeStep {
					private int size = 4;
					private int type = GL_FLOAT;
					private boolean normalized;
					private int stride;
					private long pointer;

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
					public StrideStep normalized(boolean normalized) {
						this.normalized = normalized;
						return this;
					}

					@Override
					public SpecifyStep stride(int stride, long pointer) {
						this.stride = stride;
						this.pointer = pointer;
						return this;
					}

					@Override
					public AttribBuilder<?> specify() {
						AttribBuilder.this.pointer = new Pointer(size, type, normalized, stride, pointer);
						return AttribBuilder.this;
					}
				}
				
				/**
				 * @param divisor
				 * @return this
				 */
				public AttribBuilder<T> divisor(int divisor) {
					this.divisor = divisor;
					return this;
				}
				
				private VertexBuilder enable() {
					return enableAttribArray(build());
				}
			}
			
			/**
			 *
			 */
			public final class AttribVecBuilder extends AttribBuilder<Vertex.AttribVec> {
				/**
				 * @param index
				 */
				private AttribVecBuilder(int index) {
					super(index);
				}

				@Override
				protected AttribVec build() {
					return new AttribVec(index, pointer, divisor);
				}
			}
			
			/**
			 *
			 */
			public final class AttribMat4Builder extends AttribBuilder<Vertex.AttribMat4> {
				/**
				 * @param index
				 */
				private AttribMat4Builder(int index) {
					super(index);
				}

				@Override
				protected AttribMat4 build() {
					return new AttribMat4(index, pointer, divisor);
				}
			}
		}
		
		/**
		 * @param index
		 * @return this
		 */
		public Builder index(VertexBufferObject index) {
			if (Objects.requireNonNull(index).target() != GL_ELEMENT_ARRAY_BUFFER) {
				throw new IllegalArgumentException("Index buffer is of invalid type: " + index.target());
			}
			this.index = index;
			return this;
		}
		
		/**
		 * @param builder
		 * @return this
		 */
		public Builder index(Consumer<IndexBufferBuilder> builder) {
			builder.andThen(IndexBufferBuilder::bind).accept(new IndexBufferBuilder());
			return this;
		}
		
		/**
		 *
		 */
		public static class VertexBufferBuilder extends VertexBufferObject.Builder<VertexBufferBuilder> {
			/**
			 * 
			 */
			private VertexBufferBuilder() {
				super(GL_ARRAY_BUFFER);
			}

			@Override
			protected VertexBufferBuilder self() {
				return this;
			}
		}
		
		/**
		 *
		 */
		public final class IndexBufferBuilder extends VertexBufferObject.Builder<IndexBufferBuilder> {
			/**
			 * 
			 */
			private IndexBufferBuilder() {
				super(GL_ELEMENT_ARRAY_BUFFER);
			}
			
			@Override
			protected IndexBufferBuilder self() {
				return this;
			}
			
			/**
			 * @return {@link Builder}
			 */
			private Builder bind() {
				return index(super.build());
			}
		}
	}
	
}
