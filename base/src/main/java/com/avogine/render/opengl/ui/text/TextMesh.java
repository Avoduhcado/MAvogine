package com.avogine.render.opengl.ui.text;

import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;

import com.avogine.render.model.mesh.Renderable;
import com.avogine.render.opengl.*;
import com.avogine.render.opengl.model.mesh.data.Vertex.Attrib;

/**
 *
 */
public class TextMesh implements Renderable {
	
	private static final Attrib VERTEX_ATTRIB = new Attrib(0, Attrib.POINTER_4F);
	
	private final VAO vao;
	private final VBO vbo;
	private int vertexCount;
	
	/**
	 * @param bufferSize 
	 */
	public TextMesh(long bufferSize) {
		vao = new VAO();
		vbo = VBO.arrayBuffer(bufferSize);
		VERTEX_ATTRIB.enable();
		
		vao.unbind();
	}
	
	@Override
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
	
	@Override
	public void render() {
		vao.bind();
		draw();
	}
}
