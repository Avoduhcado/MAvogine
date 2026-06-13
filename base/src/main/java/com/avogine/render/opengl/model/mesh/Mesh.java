package com.avogine.render.opengl.model.mesh;

import static org.lwjgl.opengl.GL11C.*;

import java.util.List;
import java.util.function.Consumer;

import org.joml.primitives.AABBf;

import com.avogine.render.model.mesh.*;
import com.avogine.render.opengl.VertexArrayObject;

/**
 *
 */
public abstract class Mesh implements Renderable, Boundable {

	protected final VertexArrayObject vao;
	protected final int vertexCount;
	protected final AABBf boundingBox;
	
	protected Mesh(VertexArrayObject vao, int vertexCount, AABBf boundingBox) {
		this.vao = vao;
		this.vertexCount = vertexCount;
		this.boundingBox = boundingBox;
	}
	
	@Override
	public void render() {
		vao.bind();
		draw();
	}
	
	@Override
	public void cleanup() {
		vao.cleanup();
	}
	
	/**
	 * Bind this mesh, and then issue a separate draw for each element after applying the action per element.
	 * 
	 * @param <T> The type of element to process for this bulk render operation.
	 * @param elements A list of elements to process before drawing.
	 * @param action The action to perform on each element before drawing.
	 */
	public <T> void render(List<T> elements, Consumer<T> action) {
		vao.bind();
		for (var element : elements) {
			action.accept(element);
			draw();
		}
	}
	
	protected void draw() {
		glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
	}
	
	/**
	 * @return the vao
	 */
	public VertexArrayObject getVao() {
		return vao;
	}
	
	/**
	 * @return the vertexCount
	 */
	public int getVertexCount() {
		return vertexCount;
	}
	
	@Override
	public AABBf getBoundingBox() {
		return boundingBox;
	}
}
