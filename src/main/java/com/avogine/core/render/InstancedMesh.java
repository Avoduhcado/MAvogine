package com.avogine.core.render;

public class InstancedMesh extends Mesh {

	public InstancedMesh(float[] positions, float[] textureCoords, float[] normals, int[] indices, int numberOfInstances) {
		super(positions, textureCoords, normals, indices);
	}

}
