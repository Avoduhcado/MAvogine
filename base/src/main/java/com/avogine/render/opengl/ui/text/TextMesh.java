package com.avogine.render.opengl.ui.text;

import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

import com.avogine.render.opengl.VAO;

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
			vao = VAO.gen(builder -> builder
					.buffer()
						.dynamic().draw()
						.data(vertexData)
						.bind()
					.vertexAttribArray(0)
						.pointer().tightlyPacked()
						.enable());
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
		vao.bindBuffer(0, vertexBuffer -> vertexBuffer.bufferSubData(vertexData));
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
