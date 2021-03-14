package com.avogine.render.data;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.lwjgl.system.*;

/**
 *
 */
public class RawMesh implements Renderable {

	private int vao;
	private int vbo;
	private int ebo;

	private float boundingRadius;
	private int vertexCount;

	private Material material;
	
	/**
	 * @param positions
	 */
	public RawMesh(FloatBuffer positions) {
		calculateBoundingRadius(positions);
		
		vao = glGenVertexArrays();
		vbo = glGenBuffers();
		ebo = glGenBuffers();
		
		addAttribute(0, positions, 3);
		
		vertexCount = positions.limit() / 3;
	}

	/**
	 * @param positions
	 */
	public RawMesh(float[] positions) {
		calculateBoundingRadius(positions);
		
		vao = glGenVertexArrays();
		vbo = glGenBuffers();
		ebo = glGenBuffers();
		
		addAttribute(0, positions, 3);
		
		vertexCount = positions.length / 3;
	}
	
	@Override
	public void bind() {
		bindMaterial();
		glBindVertexArray(vao);
	}

	@Override
	public void unbind() {
		glBindVertexArray(0);
		unbindMaterial();
	}

	/**
	 * If this mesh has an associated material, and that material either has an attached texture or normal map, bind them.
	 */
	private void bindMaterial() {
		if (material == null) {
			return;
		}
		if (material.isTextured()) {
			glActiveTexture(GL_TEXTURE0);
			material.getDiffuse().bind();
		}
		if (material.hasNormalMap()) {
			glActiveTexture(GL_TEXTURE1);
			material.getNormalMap().bind();
		}
	}
	
	private void unbindMaterial() {
		if (material == null) {
			return;
		}
		if (material.isTextured()) {
			// XXX Do we need to call active texture?
			glBindTexture(GL_TEXTURE_2D, 0);
		}
	}
	
	@Override
	public void render() {
		bind();
		
		glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
		
		unbind();
	}

	@Override
	public <T> void renderBatch(Collection<T> entities, Consumer<T> consumer) {
		renderBatch(entities.stream(), consumer);
	}

	@Override
	public <T> void renderBatch(Stream<T> entities, Consumer<T> consumer) {
		bind();

		entities
//		.filter(T::isInsideFrustum)
		.forEach(entity -> {
			// Set up data required by entity
			consumer.accept(entity);
			// Render this entity
			glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
		});

		unbind();
	}

	/**
	 * Generate and load a new {@code Vertex Buffer Object} into this mesh's {@code Vertex Array Object}.
	 * @param location The index this VBO should occupy in the mesh's VAO
	 * @param data The actual vertex data to load
	 * @param size How many values are associated per vertex, {@code data / size} should return a whole number
	 */
	public void addAttribute(int location, FloatBuffer data, int size) {
		bind();
		
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);

		glEnableVertexAttribArray(location);
		glVertexAttribPointer(location, size, GL_FLOAT, false, size * Float.BYTES, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		unbind();
	}
	
	/**
	 * Generate and load a new {@code Vertex Buffer Object} into this mesh's {@code Vertex Array Object}.
	 * <p>
	 * This method will allocate and free a new {@link FloatBuffer} as directly loading float arrays is not preferred when loading buffer data. 
	 * @param location The index this VBO should occupy in the mesh's VAO
	 * @param data The actual vertex data to load
	 * @param size How many values are associated per vertex, {@code data / size} should return a whole number
	 */
	public void addAttribute(int location, float[] data, int size) {
		FloatBuffer floatBuffer = null;
		try {
			floatBuffer = MemoryUtil.memAllocFloat(data.length);
			floatBuffer.put(data).flip();
			
			addAttribute(location, floatBuffer, size);
		} finally {
			if (floatBuffer != null) {
				MemoryUtil.memFree(floatBuffer);
			}
		}
	}
	
	/**
	 * @param location
	 * @param data
	 * @param size
	 */
	public void addIntAttribute(int location, IntBuffer data, int size) {
		bind();
		
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);

		glEnableVertexAttribArray(location);
		glVertexAttribIPointer(location, size, GL_INT, size * Integer.BYTES, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		unbind();
	}
	
	/**
	 * @param location
	 * @param data
	 * @param size
	 */
	public void addIntAttribute(int location, int[] data, int size) {
		IntBuffer intBuffer = null;
		try {
			intBuffer = MemoryUtil.memAllocInt(data.length);
			intBuffer.put(data).flip();
			
			addIntAttribute(location, intBuffer, size);
		} finally {
			if (intBuffer != null) {
				MemoryUtil.memFree(intBuffer);
			}
		}
	}
	
	/**
	 * @param indices
	 * @param size
	 */
	public void addIndexAttribute(IntBuffer indices, int size) {
		bind();
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
		
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
		
		vertexCount = size;
		
		unbind();
	}
	
	/**
	 * @param indices
	 */
	public void addIndexAttribute(int[] indices) {
		IntBuffer indexBuffer = null;
		try {
			indexBuffer = MemoryUtil.memAllocInt(indices.length);
			indexBuffer.put(indices).flip();
			
			addIndexAttribute(indexBuffer, indices.length);
		} finally {
			if(indexBuffer != null) {
				MemoryUtil.memFree(indexBuffer);
			}
		}
	}
	
	private void calculateBoundingRadius(FloatBuffer positions) {
		boundingRadius = 0;
		while (positions.hasRemaining()) {
			float pos = positions.get();
			boundingRadius = Math.max(pos, boundingRadius);
		}
		positions.rewind();
	}
	
	private void calculateBoundingRadius(float positions[]) {
		boundingRadius = 0;
		for (int i = 0; i < positions.length; i++) {
			float pos = positions[i];
			boundingRadius = Math.max(Math.abs(pos), boundingRadius);
		}
	}

	/**
	 * @return
	 */
	public float getBoundingRadius() {
		return boundingRadius;
	}
	
	/**
	 * @return
	 */
	public float getBoundingRadiusSquared() {
		return boundingRadius * boundingRadius;
	}
	
	/**
	 * @param boundingRadius
	 */
	public void setBoundingRadius(float boundingRadius) {
		this.boundingRadius = boundingRadius;
	}
	
	/**
	 * @return the number of vertices in this mesh
	 */
	public int getVertexCount() {
		return vertexCount;
	}
	
	/**
	 * @return
	 */
	public Material getMaterial() {
		return material;
	}

	/**
	 * @param material
	 */
	public void setMaterial(Material material) {
		this.material = material;
	}

	@Override
	public void cleanup() {
		glDeleteBuffers(vbo);
		glDeleteBuffers(ebo);
		
		glBindVertexArray(0);
		glDeleteVertexArrays(vao);
	}
	
}
