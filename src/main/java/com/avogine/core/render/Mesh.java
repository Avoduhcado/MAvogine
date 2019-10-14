package com.avogine.core.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import com.avogine.core.entity.GameObject;

public class Mesh {

	/**
	 * Maximum number of weights that can affect a single joint during an animation.
	 */
	public static final int MAX_WEIGHTS = 4;

	protected int vao;
	
	protected List<Integer> vboList = new ArrayList<>();
	protected int indexVbo;
	
	protected int vertexCount;
	
	protected Material material;

	private float boundingRadius;

	public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices) {
		this(positions, textCoords, normals, createEmptyFloatArray(MAX_WEIGHTS * positions.length / 3, 0), createEmptyIntArray(MAX_WEIGHTS * positions.length / 3, 0), indices);
	}
	
	public Mesh(float[] positions, float[] textCoords, float[] normals, float[] weights, int[] jointIndices, int[] indices) {
		calculateBoundingRadius(positions);
		
		vao = GL30.glGenVertexArrays();
		bind();

		createAttribute(0, positions, 3);
		createAttribute(1, textCoords, 2);
		createAttribute(2, normals, 3);
		createAttribute(3, weights, 4);
		createIntAttribute(4, jointIndices, 4);
		createIndexAttribute(indices);

		unbind();

		vertexCount = indices.length;
	}
	
	private void calculateBoundingRadius(float positions[]) {
		int length = positions.length;
		boundingRadius = 0;
		for(int i=0; i< length; i++) {
			float pos = positions[i];
			boundingRadius = Math.max(Math.abs(pos), boundingRadius);
		}
	}
	
	protected void initRender() {
		if (material.isTextured()) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			material.getTexture().bind();
		}
		if (material.hasNormalMap()) {
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			material.getNormalMap().bind();
		}
		bind(0, 1, 2, 3, 4);
	}

	protected void endRender() {
		unbind(0, 1, 2, 3, 4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	public void render() {
		initRender();
		
		// Draw the vertices
		GL11.glDrawElements(GL11.GL_TRIANGLES, getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

		endRender();
	}
	
	public void renderList(List<GameObject> entities, Consumer<GameObject> consumer) {
		initRender();

		for(GameObject entity : entities) {
			if (!entity.isInsideFrustum()) {
				continue;
			}
			// Set up data required by entity
			consumer.accept(entity);
			// Render this entity
			GL11.glDrawElements(GL11.GL_TRIANGLES, getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		}

		endRender();
	}
	
	/**
	 * Bind the VAO and enable any VBO attributes necessary to render the {@link Mesh} with.
	 * @param attributes
	 */
	protected void bind(int... attributes) {
		bind();
		for (int i : attributes) {
			GL20.glEnableVertexAttribArray(i);
		}
	}

	protected void bind() {
		GL30.glBindVertexArray(vao);
	}

	/**
	 * Unbind the VAO and disable the VBO attributes that were being used.
	 * @param attributes
	 */
	protected void unbind(int... attributes) {
		for (int i : attributes) {
			GL20.glDisableVertexAttribArray(i);
		}
		unbind();
	}

	protected void unbind() {
		GL30.glBindVertexArray(0);
	}
	
	protected void createIndexAttribute(int[] indices) {
		indexVbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexVbo);
		
		IntBuffer indexBuffer = null;
		try {
			indexBuffer = MemoryUtil.memAllocInt(indices.length);
			indexBuffer.put(indices).flip();
			
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);
		} finally {
			if(indexBuffer != null) {
				MemoryUtil.memFree(indexBuffer);
			}
		}
	}

	protected void createAttribute(int attribute, float[] data, int attrSize){
		int vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		
		FloatBuffer floatBuffer = null;
		try {
			floatBuffer = MemoryUtil.memAllocFloat(data.length);
			floatBuffer.put(data).flip();
			
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatBuffer, GL15.GL_STATIC_DRAW);
		} finally {
			if (floatBuffer != null) {
				MemoryUtil.memFree(floatBuffer);
			}
		}
		
		GL20.glVertexAttribPointer(attribute, attrSize, GL11.GL_FLOAT, false, attrSize * Float.BYTES, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		vboList.add(vbo);
	}

	protected void createIntAttribute(int attribute, int[] data, int attrSize) {
		int vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		
		IntBuffer intBuffer = null;
		try {
			intBuffer = MemoryUtil.memAllocInt(data.length);
			intBuffer.put(data).flip();
			
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, intBuffer, GL15.GL_STATIC_DRAW);
		} finally {
			if (intBuffer != null) {
				MemoryUtil.memFree(intBuffer);
			}
		}

		GL30.glVertexAttribIPointer(attribute, attrSize, GL11.GL_INT, attrSize * Integer.BYTES, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		vboList.add(vbo);
	}
	
	public void cleanup() {
		GL30.glDisableVertexAttribArray(0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		for (Integer vbo : vboList) {
			GL15.glDeleteBuffers(vbo);
		}
		GL15.glDeleteBuffers(indexVbo);
		
		// Delete VAO
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vao);
	}
	
	public int getVao() {
		return vao;
	}
	
	public int getVertexCount() {
		return vertexCount;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public void setMaterial(Material material) {
		this.material = material;
	}

	public float getBoundingRadius() {
		return boundingRadius;
	}

	public void setBoundingRadius(float boundingRadius) {
		this.boundingRadius = boundingRadius;
	}
	
	protected static float[] createEmptyFloatArray(int length, float defaultValue) {
		float[] result = new float[length];
		Arrays.fill(result, defaultValue);
		return result;
	}

	protected static int[] createEmptyIntArray(int length, int defaultValue) {
		int[] result = new int[length];
		Arrays.fill(result, defaultValue);
		return result;
	}

}
