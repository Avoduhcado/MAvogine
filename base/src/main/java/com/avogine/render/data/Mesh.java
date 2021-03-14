package com.avogine.render.data;

import java.lang.invoke.MethodHandles;
import java.nio.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.*;

import static org.lwjgl.opengl.GL33.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class Mesh implements Renderable {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

	private int vao;
	private int vbo;
	private int ebo;
	
	private int vertexCount;
	
	private Material material;
	
	/**
	 * 
	 * @param data
	 * @param indices
	 */
	public Mesh(FloatBuffer data, IntBuffer indices) {
		vao = glGenVertexArrays();
		vbo = glGenBuffers();
		ebo = glGenBuffers();
		
		bind();
		
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);

		glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
		glEnableVertexAttribArray(2);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		// XXX Don't unbind this, or make sure to rebind it before calling drawElements
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
		
		vertexCount = indices.limit();
		
		unbind();
	}
	
	/**
	 * Bind the {@link #material} and {@code Vertex Array} for this mesh.
	 * <p>
	 * This is called automatically by {@link #render()}.
	 */
	@Override
	public void bind() {
		bindMaterial();
		glBindVertexArray(vao);
	}
	
	/**
	 * Unbind the {@code Vertex Array} and unbind any material textures if they exist.
	 */
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
		if (material.getSpecular() != null) {
			glActiveTexture(GL_TEXTURE1);
			material.getSpecular().bind();
		}
		if (material.hasNormalMap()) {
			glActiveTexture(GL_TEXTURE2);
			material.getNormalMap().bind();
		}
	}
	
	private void unbindMaterial() {
		if (material == null) {
			return;
		}
		if (material.isTextured()) {
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, 0);
		}
		if (material.getSpecular() != null) {
			glActiveTexture(GL_TEXTURE1);
			glBindTexture(GL_TEXTURE_2D, 0);
		}
		if (material.hasNormalMap()) {
			glActiveTexture(GL_TEXTURE2);
			glBindTexture(GL_TEXTURE_2D, 0);
		}
	}
	
	/**
	 * Draw the vertices of this mesh to the currently bound {@code FrameBuffer}.
	 * <p>
	 * <b>TODO: Importing things for openGL doc doesn't work with static imports?</b>
	 * <p>
	 * This is a simple wrapper to {@link GL11#glDrawElements(GL11.GL_TRIANGLES, int, GL11.GL_UNSIGNED_INT, long)} that handles calling
	 * {@link #bind()} and {@link #unbind()} before and after drawing to perform any necessary {@code Texture} and {@code Vertex Array} binding.
	 */
	@Override
	public void render() {
		bind();
		
		glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
		
		unbind();
	}
	
	/**
	 * 
	 * @param <T>
	 * @param entities
	 * @param consumer
	 */
	@Override
	public <T> void renderBatch(Collection<T> entities, Consumer<T> consumer) {
		renderBatch(entities.stream(), consumer);
	}
	
	/**
	 * 
	 * @param <T>
	 * @param entities
	 * @param consumer
	 */
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
	
	/**
	 * @param length
	 * @param defaultValue
	 * @return
	 */
	public static float[] createEmptyFloatArray(int length, float defaultValue) {
		float[] result = new float[length];
		Arrays.fill(result, defaultValue);
		return result;
	}

	/**
	 * @param length
	 * @param defaultValue
	 * @return
	 */
	public static int[] createEmptyIntArray(int length, int defaultValue) {
		int[] result = new int[length];
		Arrays.fill(result, defaultValue);
		return result;
	}
	
}
