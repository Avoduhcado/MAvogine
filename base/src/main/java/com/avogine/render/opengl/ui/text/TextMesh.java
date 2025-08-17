package com.avogine.render.opengl.ui.text;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;

import java.nio.FloatBuffer;
import java.util.function.Function;

import org.lwjgl.system.MemoryUtil;

import com.avogine.render.opengl.*;

/**
 *
 */
public class TextMesh extends VertexArrayObject {
	
	private static final Function<FloatBuffer, Builder> TEXT_VAO = vertexData -> {
		try (var builder = new Builder()) {
			return builder
					.buffer(VertexBufferObject.arrayBufferWithUsage(GL_DYNAMIC_DRAW, vertexData))
					.attrib(VertexAttrib.array(0).pointer(VertexAttrib.Format.tightlyPackedUnnormalizedFloat(4)));
		} finally {
			MemoryUtil.memFree(vertexData);
		}
	};
	
	private int activeVertexCount;
	
	/**
	 * @param vertexData
	 */
	public TextMesh(FloatBuffer vertexData) {
		super(TEXT_VAO.apply(vertexData));
	}

	/**
	 * @param vertexData
	 */
	public void updateText(FloatBuffer vertexData) {
		activeVertexCount = vertexData.limit() / 4;
		getVertexBufferObjects()[0].bind().bufferSubData(vertexData);
	}

	@Override
	public void draw() {
		glDrawArrays(GL_TRIANGLES, 0, activeVertexCount);
	}
	
}
