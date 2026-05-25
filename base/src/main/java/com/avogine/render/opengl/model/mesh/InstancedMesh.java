package com.avogine.render.opengl.model.mesh;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;

import java.nio.Buffer;
import java.util.*;
import java.util.function.ObjIntConsumer;

import org.joml.primitives.AABBf;

import com.avogine.render.model.mesh.data.*;
import com.avogine.render.opengl.VertexArrayObject;
import com.avogine.render.opengl.model.mesh.data.InstanceData.Vertex4x4fInstanced;
import com.avogine.render.opengl.model.mesh.data.VertexData.*;
import com.avogine.render.opengl.model.mesh.data.VertexData.Vertex.*;
import com.avogine.render.util.Instanceable;

/**
 *
 */
@SuppressWarnings("java:S107")
public final class InstancedMesh extends Mesh implements Instanceable {
	private final int maxInstances;
	
	/**
	 * @param positions
	 * @param normals
	 * @param tangents nullable
	 * @param bitangents nullable
	 * @param textureCoordinates
	 * @param indices
	 * @param aabb
	 * @param instanceTransforms
	 * @param instanceNormals
	 * @param maxInstances
	 */
	public InstancedMesh(float[] positions, float[] normals, float[] tangents, float[] bitangents, float[] textureCoordinates, int[] indices, AABBf aabb,
			float[] instanceTransforms, float[] instanceNormals, int maxInstances) {
		super(buildVAO(positions, normals, tangents, bitangents, textureCoordinates, instanceTransforms, instanceNormals, indices), indices.length, aabb);
		this.maxInstances = maxInstances;
	}
	
	/**
	 * @param positions
	 * @param normals
	 * @param tangents nullable
	 * @param bitangents nullable
	 * @param textureCoordinates
	 * @param indices
	 * @param aabb
	 * @param maxInstances
	 */
	public InstancedMesh(float[] positions, float[] normals, float[] tangents, float[] bitangents, float[] textureCoordinates, int[] indices, AABBf aabb, int maxInstances) {
		super(buildVAO(positions, normals, tangents, bitangents, textureCoordinates, new float[maxInstances * 16], new float[maxInstances * 16], indices), indices.length, aabb);
		this.maxInstances = maxInstances;
	}
	
	/**
	 * @param meshData 
	 * @param instanceMeshData 
	 */
	public InstancedMesh(MeshData meshData, InstanceMeshData instanceMeshData) {
		this(meshData.vertices(), meshData.normals(), meshData.tangents(), meshData.bitangents(), meshData.textureCoordinates(), meshData.indices(), meshData.aabb(),
				instanceMeshData.instanceTransforms(), instanceMeshData.instanceNormals(), instanceMeshData.maxInstances());
	}
	
	private static VertexArrayObject buildVAO(float[] positions, float[] normals, float[] tangents, float[] bitangents, float[] textureCoordinates, 
			float[] instanceTransforms, float[] instanceNormals, int[] indices) {
		try (Vertex3D positionsVertex = Vertex.wrap3D(positions, 0);
				Vertex3D normalsVertex = Vertex.wrap3D(normals, 1);
				Vertex3D tangentsVertex = Vertex.wrap3D(Objects.isNull(tangents) ? normals.length : tangents, 2);
				Vertex3D bitangentsVertex = Vertex.wrap3D(Objects.isNull(bitangents) ? normals.length : bitangents, 3);
				Vertex2D textureCoordinatesVertex = Vertex.wrap2D(textureCoordinates, 4);
				Vertex4x4fInstanced instanceTransformsVertex = Vertex4x4fInstanced.wrap(instanceTransforms, 5);
				Vertex4x4fInstanced instanceNormalsVertex = Vertex4x4fInstanced.wrap(instanceNormals, 9);
				Index indicesIndex = Index.wrap(indices);) {
			return VertexArrayObject.gen(array -> array
					.vertex(positionsVertex)
					.vertex(normalsVertex)
					.vertex(tangentsVertex)
					.vertex(bitangentsVertex)
					.vertex(textureCoordinatesVertex)
					.vertex(instanceTransformsVertex)
					.vertex(instanceNormalsVertex)
					.elements(indicesIndex));
		}
	}
	
	@Override
	protected void draw() {
		glDrawElementsInstanced(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0, getMaxInstances());
	}
	
	@Override
	public <T extends Buffer> void updateInstanceBuffer(int vboIndex, long offset, T data) {
		getVAO().bindVBO(vboIndex, vbo -> vbo.bufferSubData(offset, data));
	}
	
	/**
	 * @param <T>
	 * @param elements
	 * @param action
	 */
	public <T> void update(List<T> elements, ObjIntConsumer<T> action) {
		getVAO().bind();
		for (int i = 0; i < elements.size(); i++) {
			action.accept(elements.get(i), i);
		}
	}
	
	@Override
	public int getMaxInstances() {
		return maxInstances;
	}
}
