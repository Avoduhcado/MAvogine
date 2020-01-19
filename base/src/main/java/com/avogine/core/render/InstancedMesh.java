package com.avogine.core.render;

public class InstancedMesh extends Mesh {

	public InstancedMesh(float[] positions, float[] textureCoords, float[] normals, int[] indices, int numberOfInstances) {
		super(positions);
		addAttribute(1, textureCoords, 2);
		addAttribute(2, normals, 3);
		addIndexAttribute(indices);
	}

}
