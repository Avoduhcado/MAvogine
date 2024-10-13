package com.avogine.render.data;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.*;
import java.util.*;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

/**
 * Used by {@link Model}.
 */
public class Mesh {
	
	private Vector3f aabbMax;
	private Vector3f aabbMin;
	private int numVertices;
	private int vaoId;
	private List<Integer> vboIdList;
	private int materialIndex;
	
	/**
	 * @param positions
	 * @param normals
	 * @param tangents
	 * @param bitangents
	 * @param textureCoordinates
	 * @param indices
	 * @param materialIndex 
	 */
	public Mesh(float[] positions, float[] normals, float[] tangents, float[] bitangents, float[] textureCoordinates, int[] indices, int materialIndex) {
		this(positions, normals, tangents, bitangents, textureCoordinates, indices, materialIndex, new Vector3f(), new Vector3f());
	}
	
	/**
	 * TODO Add animation vertex data
	 * @param positions
	 * @param normals
	 * @param tangents
	 * @param bitangents
	 * @param textureCoordinates
	 * @param indices
	 * @param materialIndex 
	 * @param aabbMin 
	 * @param aabbMax 
	 */
	public Mesh(float[] positions, float[] normals, float[] tangents, float[] bitangents, float[] textureCoordinates, int[] indices, int materialIndex, Vector3f aabbMin, Vector3f aabbMax) {
		FloatBuffer positionsBuffer = null;
		FloatBuffer normalsBuffer = null;
		FloatBuffer tangentsBuffer = null;
		FloatBuffer bitangentsBuffer = null;
		FloatBuffer textCoordsBuffer = null;
		IntBuffer indicesBuffer = null;
		try {
			this.aabbMin = aabbMin;
			this.aabbMax = aabbMax;
			numVertices = indices.length;
			vboIdList = new ArrayList<>();
			this.materialIndex = materialIndex;

			vaoId = glGenVertexArrays();
			glBindVertexArray(vaoId);

			// Positions VBO
			int vboId = glGenBuffers();
			vboIdList.add(vboId);
			positionsBuffer = MemoryUtil.memCallocFloat(positions.length);
			positionsBuffer.put(0, positions);
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
			glEnableVertexAttribArray(0);
			glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

			// Normals VBO
			vboId = glGenBuffers();
			vboIdList.add(vboId);
			normalsBuffer = MemoryUtil.memCallocFloat(normals.length);
			normalsBuffer.put(0, normals);
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);
			glEnableVertexAttribArray(1);
			glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

			// Tangents VBO
			vboId = glGenBuffers();
			vboIdList.add(vboId);
			tangentsBuffer = MemoryUtil.memCallocFloat(tangents.length);
			tangentsBuffer.put(0, tangents);
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferData(GL_ARRAY_BUFFER, tangentsBuffer, GL_STATIC_DRAW);
			glEnableVertexAttribArray(2);
			glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

			// Bitangents VBO
			vboId = glGenBuffers();
			vboIdList.add(vboId);
			bitangentsBuffer = MemoryUtil.memCallocFloat(bitangents.length);
			bitangentsBuffer.put(0, bitangents);
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferData(GL_ARRAY_BUFFER, bitangentsBuffer, GL_STATIC_DRAW);
			glEnableVertexAttribArray(3);
			glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0);

			// Texture coordinates VBO
			vboId = glGenBuffers();
			vboIdList.add(vboId);
			textCoordsBuffer = MemoryUtil.memCallocFloat(textureCoordinates.length);
			textCoordsBuffer.put(0, textureCoordinates);
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
			glEnableVertexAttribArray(4);
			glVertexAttribPointer(4, 2, GL_FLOAT, false, 0, 0);
			
			// Index VBO
			vboId = glGenBuffers();
			vboIdList.add(vboId);
			indicesBuffer = MemoryUtil.memCallocInt(indices.length);
			indicesBuffer.put(0, indices);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

			glBindBuffer(GL_ARRAY_BUFFER, 0);
			glBindVertexArray(0);
		} finally {
			if (positionsBuffer != null) {
				MemoryUtil.memFree(positionsBuffer);
			}
			if (normalsBuffer != null) {
				MemoryUtil.memFree(normalsBuffer);
			}
			if (tangentsBuffer != null) {
				MemoryUtil.memFree(tangentsBuffer);
			}
			if (bitangentsBuffer != null) {
				MemoryUtil.memFree(bitangentsBuffer);
			}
			if (textCoordsBuffer != null) {
				MemoryUtil.memFree(textCoordsBuffer);
			}
			if (indicesBuffer != null) {
				MemoryUtil.memFree(indicesBuffer);
			}
		}
	}

	/**
	 * Free all GPU memory.
	 */
	public void cleanup() {
		vboIdList.forEach(GL30::glDeleteBuffers);
		glDeleteVertexArrays(vaoId);
	}

	/**
	 * @return the maximum position of the axis-aligned bounding box that contains this mesh.
	 */
	public Vector3f getAabbMax() {
		return aabbMax;
	}

	/**
	 * @return the minimum position of the axis-aligned bounding box that contains this mesh.
	 */
	public Vector3f getAabbMin() {
		return aabbMin;
	}

	/**
	 * @return the total number of vertex indices used to construct this mesh.
	 */
	public int getNumVertices() {
		return numVertices;
	}

	/**
	 * @return the ID of the vertex array object this mesh is bound to in memory.
	 */
	public final int getVaoId() {
		return vaoId;
	}
	
	/**
	 * @return the materialIndex
	 */
	public int getMaterialIndex() {
		return materialIndex;
	}
	
}
