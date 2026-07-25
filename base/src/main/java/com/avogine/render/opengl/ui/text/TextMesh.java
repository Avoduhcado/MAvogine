package com.avogine.render.opengl.ui.text;

import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;
import java.util.Set;

import com.avogine.render.opengl.*;
import com.avogine.render.opengl.model.mesh.data.Vertex;

/**
 *
 */
public class TextMesh {
	
	private final VAO vao;
	private final VBO vbo;
	private int vertexCount;
	
	/**
	 * @param bufferSize 
	 */
	public TextMesh(long bufferSize) {
		vbo = VBO.arrayBuffer(bufferSize);
		vao = new VAO(Set.of(Vertex.vertex4D(vbo, 0)));
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		vbo.cleanup();
		vao.cleanup();
	}
	
	/**
	 * @param vertexData
	 */
	public void update(FloatBuffer vertexData) {
		vao.bind();
		
		vbo.bind();
		vbo.bufferSubData(vertexData);
		vbo.unbind();
		
		vertexCount = vertexData.limit() / 4;
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
