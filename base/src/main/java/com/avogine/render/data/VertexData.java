package com.avogine.render.data;

/**
 * @param positions 
 * @param normals 
 * @param tangents 
 * @param bitangents 
 * @param textureCoordinates 
 * @param indices 
 */
public record VertexData(float[] positions, float[] normals, float[] tangents, float[] bitangents, float[] textureCoordinates, int[] indices) {

}
