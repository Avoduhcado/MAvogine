package com.avogine.render.opengl.ui.text;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

import com.avogine.render.opengl.VertexArrayObject;
import com.avogine.render.opengl.ui.data.BufferVertexArrayData;

/**
 *
 */
public class TextMesh extends VertexArrayObject<BufferVertexArrayData<FloatBuffer>> {

	private int vertexCount;
	
	/**
	 * @param vertexData
	 */
	public TextMesh(BufferVertexArrayData<FloatBuffer> vertexData) {
		super(vertexData);
	}

	@Override
	protected Builder init(BufferVertexArrayData<FloatBuffer> vertexData) {
		try {
			return initVAO()
					.buffer(VertexBufferObject.arrayBufferWithUsage(GL_DYNAMIC_DRAW, vertexData.buffer()))
					.attrib(VertexAttrib.array(0).pointer(VertexAttrib.Format.tightlyPackedUnnormalizedFloat(4)));
		} finally {
			unbind();
			MemoryUtil.memFree(vertexData.buffer());
		}
	}
	
	/**
	 * @param vertexData
	 */
	public void updateText(FloatBuffer vertexData) {
		vertexCount = vertexData.limit() / 4;
		getVertexBufferObjects()[0].bind().bufferSubData(vertexData);
	}

	@Override
	public void draw() {
		glDrawArrays(GL_TRIANGLES, 0, vertexCount);
	}
	
}
