package com.avogine.render.opengl.model.mesh;

import java.nio.*;
import java.util.*;

import org.joml.primitives.AABBf;

import com.avogine.render.model.mesh.data.*;
import com.avogine.render.opengl.model.mesh.data.*;

/**
 *
 */
public class AnimatedMesh extends Mesh {
	/**
	 * 
	 */
	public static final int MAX_WEIGHTS = 4;
	
	/**
	 * @param positions
	 * @param vertexData
	 * @param skeletonData
	 * @param indices
	 * @param boundingBox
	 */
	public AnimatedMesh(FloatBuffer positions, VertexData vertexData, SkeletonData skeletonData, IntBuffer indices, AABBf boundingBox) {
		List<Vertex> vertices = new ArrayList<>(StaticMesh.assembleVertices(positions, vertexData));
		vertices.add(Vertex.boneID(skeletonData.boneIds(), 5));
		vertices.add(Vertex.vertex4D(skeletonData.weights(), 6));
		
		super(vertices, new Index(indices), boundingBox);
	}
}
