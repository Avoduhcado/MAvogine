package com.avogine.render.data;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

import java.nio.*;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.lwjgl.system.*;

/**
 * TODO Potentially include multiple VBOs for instanced meshes, one for static data, and one for dynamic data that is streamed in as the scene updates and meshes transform themselves
 * Cleanup all this code, presently this is pretty hardcoded just for rendering 2D sprites with a texture atlas
 */
public class InstancedMesh {

	private int vao;
	private int vbo;
	private int ebo;
	private int instanceVbo;

	private int vertexCount;
	private int numberOfInstances;

	private Material material;
//	private FloatBuffer instanceDataBuffer;
	
	/**
	 * @param vertexData
	 * @param instanceVertexData 
	 * @param indices
	 * @param instanceCount
	 */
	public InstancedMesh(FloatBuffer vertexData, FloatBuffer instanceVertexData, IntBuffer indices, int instanceCount) {
		this.numberOfInstances = instanceCount;

		vao = glGenVertexArrays();
		vbo = glGenBuffers();
		ebo = glGenBuffers();
		instanceVbo = glGenBuffers();
		
		bind();
		
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);

		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3L * Float.BYTES);
		glEnableVertexAttribArray(2);
		glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.BYTES, 6L * Float.BYTES);
		
		glBindBuffer(GL_ARRAY_BUFFER, instanceVbo);
		glBufferData(GL_ARRAY_BUFFER, instanceVertexData, GL_STREAM_DRAW);

		glEnableVertexAttribArray(3);
		glVertexAttribPointer(3, 4, GL_FLOAT, false, 17 * Float.BYTES, 0);
		glVertexAttribDivisor(3, 1);
		glEnableVertexAttribArray(4);
		glVertexAttribPointer(4, 4, GL_FLOAT, false, 17 * Float.BYTES, 4L * Float.BYTES);
		glVertexAttribDivisor(4, 1);
		glEnableVertexAttribArray(5);
		glVertexAttribPointer(5, 4, GL_FLOAT, false, 17 * Float.BYTES, 8L * Float.BYTES);
		glVertexAttribDivisor(5, 1);
		glEnableVertexAttribArray(6);
		glVertexAttribPointer(6, 4, GL_FLOAT, false, 17 * Float.BYTES, 12L * Float.BYTES);
		glVertexAttribDivisor(6, 1);
		glEnableVertexAttribArray(7);
		glVertexAttribPointer(7, 1, GL_FLOAT, false, 17 * Float.BYTES, 16L * Float.BYTES);
		glVertexAttribDivisor(7, 1);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		unbind();
		
		// XXX Don't unbind this, or make sure to rebind it before calling drawElements
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		
		vertexCount = indices.limit();
	}
	
	/**
	 * @param positions 
	 * @param numberOfInstances 
	 * 
	 */
	public InstancedMesh(FloatBuffer positions, int numberOfInstances) {
		this.numberOfInstances = numberOfInstances;
	}
	
	/**
	 * @param positions
	 * @param numberOfInstances
	 */
	public InstancedMesh(float[] positions, int numberOfInstances) {
		this.numberOfInstances = numberOfInstances;
	}

	public void bind() {
		glBindVertexArray(vao);
	}

	public void unbind() {
		glBindVertexArray(0);
	}

	public void render() {
		bind();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
		
		glDrawElementsInstanced(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0, numberOfInstances);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		unbind();
	}
	
	public <T> void renderBatch(Collection<T> entities, Consumer<T> action) {
		// TODO Auto-generated method stub
		
	}

	public <T> void renderBatch(Stream<T> entities, Consumer<T> action) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @see Old render doc about consumers
	 * @param data
	 */
	public void prepare(Float[] data) {
		bind();
		
		glBindBuffer(GL_ARRAY_BUFFER, instanceVbo);
		
		for (int i = 0; i < numberOfInstances; i++) {
			int offset = i * 17;
			
			try (MemoryStack stack = MemoryStack.stackPush()) {
				FloatBuffer instanceBuffer = stack.mallocFloat(17);
				for (int j = offset; j < offset + 17; j++) {
					instanceBuffer.put(data[j]);
				}
				instanceBuffer.flip();
				
				glBufferSubData(GL_ARRAY_BUFFER, offset * 4L, instanceBuffer);
			}
		}
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		unbind();
	}
	
	/**
	 * TODO This should not take in anything, or if it does it should be some kind of {@link Consumer}
	 * @param transforms
	 */
//	public void render(List<Matrix4f> transforms) {
//		bind();
//		
//		instanceDataBuffer.clear();
//		
//		for (int i = 0; i < transforms.size(); i++) {
//			transforms.get(i).get(16 * i, instanceDataBuffer);
//		}
//		
//		glBindBuffer(GL_ARRAY_BUFFER, instanceVbo);
//		glBufferData(GL_ARRAY_BUFFER, instanceDataBuffer, GL_DYNAMIC_DRAW);
//		
//		glDrawElementsInstanced(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0, numberOfInstances);
//		
//		glBindBuffer(GL_ARRAY_BUFFER, 0);
//		
//		unbind();
//	}
	
	/**
	 * @param location
	 * @param data
	 * @param size
	 */
	public void addInstancedAttribute(int location, FloatBuffer data, int size) {
		bind();
		
		int instanceVbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, instanceVbo);
		glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW); // XXX Be careful, maybe setting this as dynamic here is bad? Although most of these attributes in an instanced case are likely to change
		
		glEnableVertexAttribArray(location);
		glVertexAttribPointer(location, size, GL_FLOAT, false, size * Float.BYTES, 0);
		glVertexAttribDivisor(location, 1);
		
//		if (vboMap.containsKey(location)) {
//			logger.warn("Overwriting location: {}", location);
//		}
//		vboMap.put(location, instanceVbo);
		
		unbind();
	}
	
	/**
	 * 
	 * @param location
	 * @param data
	 * @param size
	 */
	public void addInstancedAttribute(int location, float[] data, int size) {
		FloatBuffer floatBuffer = null;
		try {
			floatBuffer = MemoryUtil.memAllocFloat(data.length);
			floatBuffer.put(data).flip();
			
			addInstancedAttribute(location, floatBuffer, size);
		} finally {
			if (floatBuffer != null) {
				MemoryUtil.memFree(floatBuffer);
			}
		}
	}
	
	/**
	 * 
	 */
	public void addDynamicInstancedAttribute(int location, int size) {
		bind();
		
		instanceVbo = glGenBuffers();
//		vboMap.put(location, instanceVbo); // TODO Should this cover all locations this block might take up?
//		instanceDataBuffer = MemoryUtil.memAllocFloat(numberOfInstances * size);
		
		glBindBuffer(GL_ARRAY_BUFFER, instanceVbo);
		
		// XXX This is a naive approach assuming if your attribute is greater than 4 floats, then it must also be evenly divisible by 4
		if (size > 4) {
			int strideStart = 0;
			for (int i = 0; i < size / 4; i++) {
				int instanceLocation = location + i;
				glEnableVertexAttribArray(instanceLocation);
				glVertexAttribPointer(instanceLocation, 4, GL_FLOAT, false, size * Float.BYTES, strideStart);
				glVertexAttribDivisor(instanceLocation, 1);
				
				strideStart += 4 * Float.BYTES;
			}
		}
		
//		if (vboMap.containsKey(location)) {
//			logger.warn("Overwriting location: {}", location);
//		}
//		vboMap.put(location, instanceVbo);
		
		unbind();
	}

	/**
	 * @return the number of vertices in this mesh
	 */
	public int getVertexCount() {
		return vertexCount;
	}
	
	/**
	 * @param material
	 */
	public void setMaterial(Material material) {
		this.material = material;
	}

	public void cleanup() {
		glDeleteBuffers(vbo);
		glDeleteBuffers(ebo);
		
		glBindVertexArray(0);
		glDeleteVertexArrays(vao);
//		if (this.instanceDataBuffer != null) {
//			MemoryUtil.memFree(this.instanceDataBuffer);
//			this.instanceDataBuffer = null;
//		}
	}
	
}
