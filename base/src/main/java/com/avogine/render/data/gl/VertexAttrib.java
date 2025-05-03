package com.avogine.render.data.gl;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

import org.lwjgl.opengl.GL20;

/**
 * @param index 
 */
public record VertexAttrib(int index) {
	
	/**
	 * 
	 */
	public VertexAttrib {
		glEnableVertexAttribArray(index);
	}
	
	/**
	 * @param index
	 * @return a new {@link VertexAttrib} for the given array index.
	 */
	public static VertexAttrib array(int index) {
		return new VertexAttrib(index);
	}
	
	/**
	 * @param size
	 * @param type
	 * @param normalized
	 * @param stride
	 * @param pointer
	 */
	public record Pointer(int size, int type, boolean normalized, int stride, long pointer) {
		/**
		 * @return a default configuration of {@link Pointer} set to OpenGL's initial values for {@link GL20#glVertexAttribPointer}
		 */
		public static Pointer initial() {
			return new Pointer(4, GL_FLOAT, false, 0, 0);
		}
		
		/**
		 * @param size the number of values per vertex that are stored in the array. The initial value is 4. One of: 1	2	3	4
		 * @return a new {@link Pointer} for un-normalized {@code float} values that are considered to be tightly packed in the buffer with specified size.
		 */
		public static Pointer tightlyPackedUnnormalizedFloat(int size) {
			return new Pointer(size, GL_FLOAT, false, 0, 0);
		}
		
		/**
		 * @param index
		 */
		public void specify(int index) {
			glVertexAttribPointer(index, size, type, normalized, stride, pointer);
		}
	}

	/**
	 * @param vertexAttribPointer 
	 * @return this
	 */
	public VertexAttrib pointer(Pointer vertexAttribPointer) {
		vertexAttribPointer.specify(index);
		return this;
	}
	
	/**
	 * @param divisor
	 * @return this
	 */
	public VertexAttrib divisor(int divisor) {
		glVertexAttribDivisor(index, divisor);
		return this;
	}
}
