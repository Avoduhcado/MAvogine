package com.avogine.render.data;

import java.util.Arrays;

/**
 * @param positions 
 * @param normals 
 * @param tangents 
 * @param bitangents 
 * @param textureCoordinates 
 * @param indices 
 */
public record VertexData(float[] positions, float[] normals, float[] tangents, float[] bitangents, float[] textureCoordinates, int[] indices) {

	@Override
	public final boolean equals(Object arg0) {
		if (this == arg0) return true;
		if (arg0 instanceof VertexData(var oP, var oN, var oT, var oB, var oTC, var oI)) {
			return Arrays.equals(positions, oP) && Arrays.equals(normals, oN) && Arrays.equals(tangents, oT) && Arrays.equals(bitangents, oB) && Arrays.equals(textureCoordinates, oTC) && Arrays.equals(indices, oI); 
		}
		return false;
	}
	
	@Override
	public final int hashCode() {
		int result = Arrays.hashCode(positions);
		result = 31 * result + Arrays.hashCode(normals);
		result = 31 * result + Arrays.hashCode(tangents);
		result = 31 * result + Arrays.hashCode(bitangents);
		result = 31 * result + Arrays.hashCode(textureCoordinates);
		result = 31 * result + Arrays.hashCode(indices);
		return result;
	}
	
	@Override
	public final String toString() {
		return "VertexData{" +
				"positions=" + Arrays.toString(positions) +
				", normals=" + Arrays.toString(normals) +
				", tangents=" + Arrays.toString(tangents) +
				", bitangents=" + Arrays.toString(bitangents) +
				", textureCoordinates=" + Arrays.toString(textureCoordinates) +
				", indices=" + Arrays.toString(indices) +
				"}";
	}
}
