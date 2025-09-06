package com.avogine.render.opengl.ui.text;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

import com.avogine.render.opengl.*;
import com.avogine.render.opengl.VAO.VAOBuilder.VertexAttrib;

/**
 *
 */
public class TextMesh {
	
	private final VAO vao;
	private int vertexCount;
	
	/**
	 * @param vertexData
	 */
	public TextMesh(FloatBuffer vertexData) {
		try {
			vao = VAO.gen(vertexArray -> vertexArray
					.bindBufferData(new VBO(GL_DYNAMIC_DRAW), vertexData)
					.enablePointer(0, VertexAttrib.Format.tightlyPackedUnnormalizedFloat(4)));
		} finally {
			MemoryUtil.memFree(vertexData);
		}
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		vao.cleanup();
	}

	/**
	 * @param vertexData
	 */
	public void updateText(FloatBuffer vertexData) {
		vertexCount = vertexData.limit() / 4;
		vao.bind();
		VBO vertexBuffer = vao.vertexBufferObjects()[0];
		vertexBuffer.bind();
		vertexBuffer.bufferSubData(vertexData);
	}

	private void draw() {
		glDrawArrays(GL_TRIANGLES, 0, vertexCount);
	}
	
	/**
	 * 
	 */
	public void render() {
		vao.bind();
		draw();
	}

}
