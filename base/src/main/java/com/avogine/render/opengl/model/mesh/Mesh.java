package com.avogine.render.opengl.model.mesh;

import static org.lwjgl.opengl.GL11C.*;

import java.util.*;
import java.util.function.Consumer;

import org.joml.primitives.AABBf;

import com.avogine.render.model.mesh.*;
import com.avogine.render.opengl.*;

/**
 *
 */
public abstract class Mesh implements Renderable, Boundable {

	protected final VAO vao;

	protected final VBO[] vbos;
	protected final VBO ebo;
	protected final int vertexCount;
	
	protected final AABBf boundingBox;
	
	protected Mesh(VAO vao, VBO[] vbos, VBO ebo, int vertexCount, AABBf boundingBox) {
		this.vao = vao;
		this.vbos = vbos;
		this.ebo = ebo;
		this.vertexCount = vertexCount;
		this.boundingBox = boundingBox;
	}
	
	@Override
	public void cleanup() {
		Arrays.stream(vbos).forEach(VBO::cleanup);
		ebo.cleanup();
		vao.cleanup();
	}
	
	@Override
	public void render() {
		vao.bind();
		draw();
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
	public VAO getVao() {
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
