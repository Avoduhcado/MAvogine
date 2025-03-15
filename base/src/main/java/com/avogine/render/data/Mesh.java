package com.avogine.render.data;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.*;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;

/**
 * Used by {@link Model}.
 * <p>
 * TODO#39 <a href="https://github.com/Avoduhcado/MAvogine/issues/39">Animated models #39</a>
 */
public class Mesh {
	
	private Vector3f aabbMax;
	private Vector3f aabbMin;
	private int numVertices;
	private int vaoId;
	private List<Integer> vboIdList;
	private int materialIndex;
	
	/**
	 * @param vertexData 
	 * @param materialIndex 
	 */
	public Mesh(VertexData vertexData, int materialIndex) {
		this(vertexData, materialIndex, new Vector3f(), new Vector3f());
	}
	
	/**
	 * @param vertexData
	 * @param materialIndex 
	 * @param aabbMin 
	 * @param aabbMax 
	 */
	public Mesh(VertexData vertexData, int materialIndex, Vector3f aabbMin, Vector3f aabbMax) {
		this.aabbMin = aabbMin;
		this.aabbMax = aabbMax;
		numVertices = vertexData.indices().remaining();
		vboIdList = new ArrayList<>();
		this.materialIndex = materialIndex;

		vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);

		// Positions VBO
		int vboId = glGenBuffers();
		vboIdList.add(vboId);
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, vertexData.positions(), GL_STATIC_DRAW);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

		// Normals VBO
		vboId = glGenBuffers();
		vboIdList.add(vboId);
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, vertexData.normals(), GL_STATIC_DRAW);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

		// Tangents VBO
		vboId = glGenBuffers();
		vboIdList.add(vboId);
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, vertexData.tangents(), GL_STATIC_DRAW);
		glEnableVertexAttribArray(2);
		glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

		// Bitangents VBO
		vboId = glGenBuffers();
		vboIdList.add(vboId);
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, vertexData.bitangents(), GL_STATIC_DRAW);
		glEnableVertexAttribArray(3);
		glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0);

		// Texture coordinates VBO
		vboId = glGenBuffers();
		vboIdList.add(vboId);
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, vertexData.textureCoordinates(), GL_STATIC_DRAW);
		glEnableVertexAttribArray(4);
		glVertexAttribPointer(4, 2, GL_FLOAT, false, 0, 0);

		// Index VBO
		vboId = glGenBuffers();
		vboIdList.add(vboId);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, vertexData.indices(), GL_STATIC_DRAW);

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}

	/**
	 * Free all GPU memory.
	 */
	public void cleanup() {
		glDeleteVertexArrays(vaoId);
		vboIdList.forEach(GL30::glDeleteBuffers);
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
