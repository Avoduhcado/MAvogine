package com.avogine.render.data.mesh;

import static org.lwjgl.opengl.GL11.*;

import org.joml.Vector3f;

import com.avogine.render.data.mesh.parameters.*;
import com.avogine.render.data.model.StaticModel;
import com.avogine.render.data.vertices.array.IndexedVertexArray;

/**
 * Used by {@link StaticModel}.
 * <p>
 * TODO#39 <a href="https://github.com/Avoduhcado/MAvogine/issues/39">Animated models #39</a>
 * @param <T> 
 */
public class StaticMesh<T extends IndexedVertexArray> extends Mesh<T> implements Materialable, Bound3D {
	
	protected int materialIndex;
	
	protected Vector3f aabbMax;
	protected Vector3f aabbMin;
	
	/**
	 * @param vertexData
	 * @param materialIndex 
	 * @param aabbMin 
	 * @param aabbMax 
	 */
	public StaticMesh(T vertexData, int materialIndex, Vector3f aabbMin, Vector3f aabbMax) {
		super(vertexData.vertexCount(), vertexData);
		this.materialIndex = materialIndex;
		
		this.aabbMin = aabbMin;
		this.aabbMax = aabbMax;
	}
	
	@Override
	public void draw() {
		glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
	}

	@Override
	public int getMaterialIndex() {
		return materialIndex;
	}
	
	@Override
	public Vector3f getAabbMax() {
		return aabbMax;
	}

	@Override
	public Vector3f getAabbMin() {
		return aabbMin;
	}
}
