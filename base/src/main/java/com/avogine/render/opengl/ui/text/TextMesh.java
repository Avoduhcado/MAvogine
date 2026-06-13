package com.avogine.render.opengl.ui.text;

import static org.lwjgl.opengl.GL11.*;

import com.avogine.render.opengl.VertexArrayObject;
import com.avogine.render.opengl.model.mesh.data.Vertex;
import com.avogine.render.opengl.model.mesh.data.Vertex.Vertex4D;

/**
 *
 */
public class TextMesh {
	
	private final VertexArrayObject vao;
	private int vertexCount;
	
	/**
	 * @param vertexData
	 */
	public TextMesh(float[] vertexData) {
		try (Vertex4D textVertex = Vertex.wrap4D(vertexData, 0)) {
			vao = VertexArrayObject.gen(mesh -> mesh
					.vertex(textVertex));
		}
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		vao.cleanup();
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
	
	/**
	 * @return the vao
	 */
	public VertexArrayObject getVAO() {
		return vao;
	}
	
	/**
	 * @param vertexCount the vertexCount to set
	 */
	public void setVertexCount(int vertexCount) {
		this.vertexCount = vertexCount;
	}
}
