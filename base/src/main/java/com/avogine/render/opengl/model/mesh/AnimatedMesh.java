package com.avogine.render.opengl.model.mesh;

import java.nio.*;
import java.util.*;

import org.joml.primitives.AABBf;

import com.avogine.render.model.mesh.data.*;
import com.avogine.render.opengl.*;
import com.avogine.render.opengl.model.mesh.data.Vertex;

/**
 *
 */
public class AnimatedMesh extends Mesh {
	/**
	 * 
	 */
	public static final int MAX_WEIGHTS = 4;
	
	private AnimatedMesh(VAO vao, VBO[] vbos, VBO ebo, int vertexCount, AABBf boundingBox) {
		super(vao, vbos, ebo, vertexCount, boundingBox);
	}
	
	/**
	 * @param positions
	 * @param vertexData
	 * @param skeletonData
	 * @param indices
	 * @param boundingBox
	 */
	public AnimatedMesh(FloatBuffer positions, VertexData vertexData, SkeletonData skeletonData, IntBuffer indices, AABBf boundingBox) {
		var meshData = StaticMesh.assembleStaticVertices(positions, vertexData, indices);
		Set<Vertex> vertices = new HashSet<>(meshData.vertices());
		vertices.add(Vertex.boneID(skeletonData.boneIds(), 5));
		vertices.add(Vertex.vertex4D(skeletonData.weights(), 6));
		var vao = new VAO(Set.copyOf(vertices), meshData.index());
		
		this(vao, meshData.vertices().stream().map(Vertex::buffer).toArray(VBO[]::new), meshData.index().buffer(), meshData.index().vertexCount(), boundingBox);
	}
}
