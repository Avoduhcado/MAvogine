package com.avogine.render.opengl.model.mesh;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;

import java.nio.Buffer;
import java.util.List;
import java.util.function.ObjIntConsumer;

import com.avogine.render.model.mesh.data.MeshData;
import com.avogine.render.util.Instanceable;

/**
 *
 */
public final class InstancedMesh extends Mesh implements Instanceable {
	private final int maxInstances;
	
	/**
	 * @param meshData 
	 */
	public InstancedMesh(MeshData meshData) {
		super(meshData);
		maxInstances = meshData.maxInstances();
	}
	
	@Override
	protected void draw() {
		glDrawElementsInstanced(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0, getMaxInstances());
	}
	
	@Override
	public <T extends Buffer> void updateInstanceBuffer(int vboIndex, long offset, T data) {
		getVao().bindBuffer(vboIndex, instanceBuffer -> instanceBuffer.bufferSubData(offset, data));
	}
	
	/**
	 * @param <T>
	 * @param elements
	 * @param action
	 */
	public <T> void update(List<T> elements, ObjIntConsumer<T> action) {
		getVao().bind();
		for (int i = 0; i < elements.size(); i++) {
			action.accept(elements.get(i), i);
		}
	}
	
	@Override
	public int getMaxInstances() {
		return maxInstances;
	}
}
