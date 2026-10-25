package com.avogine.render.opengl.model.mesh.data;

import static org.lwjgl.system.MemoryUtil.*;

import java.nio.IntBuffer;
import java.util.function.Consumer;

import com.avogine.render.opengl.VertexArrayObject.Builder.IndexBufferBuilder;

/**
 *
 * @param buffer
 */
public record Index(IntBuffer buffer) implements Consumer<IndexBufferBuilder>, AutoCloseable {
	/**
	 * @param data
	 * @return an {@link Index} wrapping the given integer array.
	 */
	public static Index wrap(int[] data) {
		IntBuffer buffer = memAllocInt(data.length);
		return new Index(buffer.put(data).flip());
	}
	
	@Override
	public void close() {
		memFree(buffer);
	}
	
	@Override
	public void accept(IndexBufferBuilder builder) {
		builder.bufferData(buffer);
	}
}
