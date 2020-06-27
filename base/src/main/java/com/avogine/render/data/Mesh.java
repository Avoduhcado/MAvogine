package com.avogine.render.data;

import java.lang.invoke.MethodHandles;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avogine.entity.Renderable;

public class Mesh {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

	private int vao;
	private Map<Integer, Integer> vboMap;
	private int indexVbo;
	
	private float boundingRadius;
	private int vertexCount;
	
	private Material material;
	
	public Mesh(FloatBuffer positions) {
		vboMap = new HashMap<>();
		calculateBoundingRadius(positions);
		
		vao = GL30.glGenVertexArrays();
		
		addAttribute(0, positions, 3);
		
		vertexCount = positions.limit() / 3;
	}
	
	public Mesh(float[] positions) {
		vboMap = new HashMap<>();
		calculateBoundingRadius(positions);
		
		vao = GL30.glGenVertexArrays();
		
		addAttribute(0, positions, 3);
		
		vertexCount = positions.length / 3;
	}
	
	public void bind() {
		bindMaterial();
		GL30.glBindVertexArray(vao);
	}
	
	public void unbind() {
		GL30.glBindVertexArray(0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	/**
	 * If this mesh has an associated material, and that material either has an attached texture or normal map, bind them.
	 */
	private void bindMaterial() {
		if (material == null) {
			return;
		}
		if (material.isTextured()) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			material.getTexture().bind();
		}
		if (material.hasNormalMap()) {
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			material.getNormalMap().bind();
		}
	}
	
	public void render() {
		bind();
		
		GL11.glDrawElements(GL11.GL_TRIANGLES, getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		
		unbind();
	}
	
	public <T extends Renderable> void renderBatch(Collection<T> entities, Consumer<T> consumer) {
		bind();

		entities.stream()
		.filter(T::isInsideFrustum)
		.forEach(entity -> {
			// Set up data required by entity
			consumer.accept(entity);
			// Render this entity
			GL11.glDrawElements(GL11.GL_TRIANGLES, getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
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
		
		int vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
		
		GL20.glEnableVertexAttribArray(location);
		GL20.glVertexAttribPointer(location, size, GL11.GL_FLOAT, false, size * Float.BYTES, 0);
		
		if (vboMap.containsKey(location)) {
			logger.warn("Overwriting location: {}", location);
		}
		vboMap.put(location, vbo);
		
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
	
	public void addIntAttribute(int location, IntBuffer data, int size) {
		bind();
		
		int vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
		
		GL30.glVertexAttribIPointer(location, size, GL11.GL_INT, size * Integer.BYTES, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		if (vboMap.containsKey(location)) {
			logger.warn("Overwriting location: {}", location);
		}
		vboMap.put(location, vbo);
		
		unbind();
	}
	
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
	
	public void addIndexAttribute(IntBuffer indices, int size) {
		bind();
		
		indexVbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexVbo);
		
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);
		
		vertexCount = size;
		
		unbind();
	}
	
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
	
	public float getBoundingRadius() {
		return boundingRadius;
	}
	
	public float getBoundingRadiusSquared() {
		return boundingRadius * boundingRadius;
	}
	
	public void setBoundingRadius(float boundingRadius) {
		this.boundingRadius = boundingRadius;
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

	public void cleanup() {
		vboMap.values().forEach(GL15::glDeleteBuffers);
		GL15.glDeleteBuffers(indexVbo);
		
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vao);
	}
	
	public static float[] createEmptyFloatArray(int length, float defaultValue) {
		float[] result = new float[length];
		Arrays.fill(result, defaultValue);
		return result;
	}

	public static int[] createEmptyIntArray(int length, int defaultValue) {
		int[] result = new int[length];
		Arrays.fill(result, defaultValue);
		return result;
	}
	
}
