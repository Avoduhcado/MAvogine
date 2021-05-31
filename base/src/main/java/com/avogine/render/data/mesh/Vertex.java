package com.avogine.render.data.mesh;

/**
 * This represents a singular vertex of a mesh.
 * 
 * @param positionX 
 * @param positionY 
 * @param positionZ 
 * @param normalX 
 * @param normalY 
 * @param normalZ 
 * @param textureCoordinateU 
 * @param textureCoordinateV 
 * 
 */
public record Vertex(float positionX, float positionY, float positionZ, float normalX, float normalY, float normalZ, float textureCoordinateU, float textureCoordinateV) {

	/**
	 * The number of elements contained in a single Vertex.
	 * <p>
	 * Currently {@value #ATTRIBUTE_SIZE}.
	 */
	public static final int ATTRIBUTE_SIZE = 8;
	
	/**
	 * Populate an array of floats with all of the attributes of this vertex aligned in the order that the {@link Mesh}
	 * VBO is expecting.
	 * @param attributes the pre-allocated array to fill
	 * @return an array of floats containing all of the vertex attributes of this vertex
	 */
	public float[] getAttributes(float[] attributes) {
		if (attributes.length < ATTRIBUTE_SIZE) {
			throw new IllegalArgumentException("Attributes array is not large enough!");
		}
		
		attributes[0] = positionX;
		attributes[1] = positionY;
		attributes[2] = positionZ;
		
		attributes[3] = normalX;
		attributes[4] = normalY;
		attributes[5] = normalZ;
		
		attributes[6] = textureCoordinateU;
		attributes[7] = textureCoordinateV;
		
		return attributes;
	}
	
}
