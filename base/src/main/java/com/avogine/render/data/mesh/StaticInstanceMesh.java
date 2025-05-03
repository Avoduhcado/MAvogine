package com.avogine.render.data.mesh;

import java.nio.Buffer;

import org.joml.Vector3f;
import org.lwjgl.opengl.*;

import com.avogine.render.data.gl.VAO;
import com.avogine.render.data.mesh.parameters.Instanceable;
import com.avogine.render.data.vertices.array.IndexedVertexArray;

/**
 * @param <T> 
 *
 */
public class StaticInstanceMesh<T extends IndexedVertexArray> extends StaticMesh<T> implements Instanceable {
	
	protected int maxInstances;
	
	/**
	 * @param vertexData 
	 * @param materialIndex 
	 * @param aabbMax 
	 * @param aabbMin 
	 * @param maxInstances 
	 */
	public StaticInstanceMesh(T vertexData, int materialIndex, Vector3f aabbMax, Vector3f aabbMin, int maxInstances) {
		super(vertexData, materialIndex, aabbMax, aabbMin);
		this.maxInstances = maxInstances;
	}
	
	@Override
	public void draw() {
		vao.bind();
		GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, getVertexCount(), GL11.GL_UNSIGNED_INT, 0, maxInstances);
		VAO.unbind();
	}

	/**
	 * @param <U>
	 * @param vboIndex
	 * @param offset
	 * @param buffer
	 */
	public <U extends Buffer> void updateInstanceBuffer(int vboIndex, long offset, U buffer) {
		vao.vertexBufferObjects().get(vboIndex).bind().bufferSubData(offset, buffer);
	}
	
	@Override
	public <U extends Buffer> void updateInstanceBuffer(int vboIndex, U buffer) {
		updateInstanceBuffer(vboIndex, 0, buffer);
	}

	@Override
	public Vector3f getAabbMax() {
		return aabbMax;
	}

	@Override
	public Vector3f getAabbMin() {
		return aabbMin;
	}

	@Override
	public int getMaxInstances() {
		return maxInstances;
	}
	
}
