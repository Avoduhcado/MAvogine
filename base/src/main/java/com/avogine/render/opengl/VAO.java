package com.avogine.render.opengl;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

import java.nio.*;
import java.util.*;
import java.util.function.*;

import org.lwjgl.opengl.GL20;

import com.avogine.logging.AvoLog;

/**
 * @param id 
 * @param vertexBufferObjects 
 */
public record VAO(int id, VBO[] vertexBufferObjects) {
	/**
	 * @param vaoInit
	 * @return a newly constructed {@link VAO} with the configurations from {@code vaoInit} applied.
	 */
	public static VAO gen(UnaryOperator<VAOBuilder> vaoInit) {
		try (VAOBuilder builder = new VAOBuilder()) {
			return vaoInit
					.andThen(VAOBuilder.TO_VAO)
					.apply(builder);
		}
	}
	
	/**
	 * Convenience method to clear the currently bound vertex array.
	 */
	public static void unbind() {
		glBindVertexArray(0);
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		Arrays.stream(vertexBufferObjects).forEach(VBO::cleanup);
		glDeleteVertexArrays(id);
	}
	
	/**
	 * 
	 */
	public void bind() {
		glBindVertexArray(id);
	}
	
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
	 * @param id
	 * @param vertexBufferObjects
	 * @param vertexAttribs
	 */
	public static record VAOBuilder(int id, List<VBO> vertexBufferObjects, Set<VertexAttrib> vertexAttribs) implements AutoCloseable {
		
		private static final Function<VAOBuilder, VAO> TO_VAO = builder -> new VAO(builder.id, builder.vertexBufferObjects.toArray(VBO[]::new));
		
		private static final Consumer<VBO> BIND_VBO = VBO::bind;
		
		private static final Consumer<VertexAttrib> ENABLE_VERTEX_ATTRIB = VertexAttrib::enable;
		
		/**
		 * Canonical constructor that binds this vertex array object upon instantiation.
		 */
		public VAOBuilder {
			glBindVertexArray(id);
		}
		
		/**
		 * 
		 */
		public VAOBuilder() {
			this(glGenVertexArrays(), new ArrayList<>(), new LinkedHashSet<>());
		}

		@Override
		public void close() {
			VAO.unbind();
		}
		
		/**
		 * @param arrayBuffer 
		 * @param bufferInit 
		 * @return this
		 */
		public VAOBuilder bind(VBO arrayBuffer, Consumer<VBO> bufferInit) {
			BIND_VBO.andThen(bufferInit.andThen(vertexBufferObjects::add)).accept(arrayBuffer);
			return this;
		}
		
		/**
		 * @param <T>
		 * @param arrayBuffer
		 * @param data
		 * @return this
		 */
		public <T extends Buffer> VAOBuilder bindBufferData(VBO arrayBuffer, T data) {
			return bind(arrayBuffer, vbo -> vbo.bufferData(data));
		}
		
		/**
		 * @param indices
		 * @return this
		 */
		public VAOBuilder bindElements(IntBuffer indices) {
			return bind(VBO.elementArrayBuffer(), ebo -> ebo.bufferData(indices));
		}
		
		/**
		 * @param vertexAttribArray 
		 * @param attribInit
		 * @return this
		 */
		public VAOBuilder enable(VertexAttrib vertexAttribArray, Consumer<VertexAttrib> attribInit) {
			if (vertexAttribs.contains(vertexAttribArray)) {
				AvoLog.log().debug("Overwriting vertex attribute at index: {}", vertexAttribArray.index);
			}
			ENABLE_VERTEX_ATTRIB.andThen(attribInit.andThen(vertexAttribs::add)).accept(vertexAttribArray);
			return this;
		}
		
		/**
		 * @param index 
		 * @param pointerFormat
		 * @return this
		 */
		public VAOBuilder enablePointer(int index, VertexAttrib.Format pointerFormat) {
			return enable(VertexAttrib.array(index), attrib -> attrib.pointer(pointerFormat));
		}
		
		/**
		 * @param index 
		 * @param pointerFormat
		 * @param divisor
		 * @return this
		 */
		public VAOBuilder enablePointerDivisor(int index, VertexAttrib.Format pointerFormat, int divisor) {
			return enable(VertexAttrib.array(index), attrib -> {
				attrib.pointer(pointerFormat);
				attrib.divisor(divisor);
			});
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
			 * Construct a new {@link VertexAttrib} for the given array index of the currently bound {@link VAO}.
			 * @param index the index of the generic vertex attribute.
			 * @return a new {@link VertexAttrib} for the given array index of the currently bound {@link VAO}.
			 */
			public static VertexAttrib array(int index) {
				return new VertexAttrib(index);
			}
			
			/**
			 * Enable this vertex attribute array.
			 */
			public void enable() {
				glEnableVertexAttribArray(index);
			}
			
			/**
			 * 
			 * @param size
			 * @param type
			 * @param normalized
			 * @param stride
			 * @param pointer
			 */
			public void pointer(int size, int type, boolean normalized, int stride, long pointer) {
				glVertexAttribPointer(index, size, type, normalized, stride, pointer);
			}
			
			/**
			 * @param format
			 */
			public void pointer(Format format) {
				switch (format) {
					case Format(var size, var type, var normalized, var stride, var pointer) -> pointer(size, type, normalized, stride, pointer);
				}
			}
			
			/**
			 * @param divisor
			 */
			public void divisor(int divisor) {
				glVertexAttribDivisor(index, divisor);
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

}
