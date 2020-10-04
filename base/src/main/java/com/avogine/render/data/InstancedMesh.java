package com.avogine.render.data;

import java.lang.invoke.MethodHandles;
import java.nio.FloatBuffer;
import java.util.List;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class InstancedMesh extends Mesh {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

	private int numberOfInstances;
	
	private int instanceVbo;
	private FloatBuffer instanceDataBuffer;
	
	/**
	 * @param positions 
	 * @param numberOfInstances 
	 * 
	 */
	public InstancedMesh(FloatBuffer positions, int numberOfInstances) {
		super(positions);
		this.numberOfInstances = numberOfInstances;
	}
	
	/**
	 * @param positions
	 * @param numberOfInstances
	 */
	public InstancedMesh(float[] positions, int numberOfInstances) {
		super(positions);
		this.numberOfInstances = numberOfInstances;
	}
	
	@Override
	public void render() {
		bind();
		
		GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, getVertexCount(), GL11.GL_UNSIGNED_INT, 0, numberOfInstances);
		
		unbind();
	}
	
	public void render(List<Matrix4f> transforms) {
		bind();
		
		instanceDataBuffer.clear();
		
		for (int i = 0; i < transforms.size(); i++) {
			transforms.get(i).get(16 * i, instanceDataBuffer);
		}
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, instanceVbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, instanceDataBuffer, GL15.GL_DYNAMIC_DRAW);
		
		GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, getVertexCount(), GL11.GL_UNSIGNED_INT, 0, numberOfInstances);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		unbind();
	}
	
	/**
	 * @param location
	 * @param data
	 * @param size
	 */
	public void addInstancedAttribute(int location, FloatBuffer data, int size) {
		bind();
		
		int instanceVbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, instanceVbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW); // XXX Be careful, maybe setting this as dynamic here is bad? Although most of these attributes in an instanced case are likely to change
		
		GL20.glEnableVertexAttribArray(location);
		GL20.glVertexAttribPointer(location, size, GL11.GL_FLOAT, false, size * Float.BYTES, 0);
		GL33.glVertexAttribDivisor(location, 1);
		
		if (vboMap.containsKey(location)) {
			logger.warn("Overwriting location: {}", location);
		}
		vboMap.put(location, instanceVbo);
		
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
		
		instanceVbo = GL15.glGenBuffers();
		vboMap.put(location, instanceVbo); // TODO Should this cover all locations this block might take up?
		instanceDataBuffer = MemoryUtil.memAllocFloat(numberOfInstances * size);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, instanceVbo);
		
		// XXX This is a naive approach assuming if your attribute is greater than 4 floats, then it must also be evenly divisible by 4
		if (size > 4) {
			int strideStart = 0;
			for (int i = 0; i < size / 4; i++) {
				int instanceLocation = location + i;
				GL20.glEnableVertexAttribArray(instanceLocation);
				GL20.glVertexAttribPointer(instanceLocation, 4, GL11.GL_FLOAT, false, size * Float.BYTES, strideStart);
				GL33.glVertexAttribDivisor(instanceLocation, 1);
				
				strideStart += 4 * Float.BYTES;
			}
		}
		
		if (vboMap.containsKey(location)) {
			logger.warn("Overwriting location: {}", location);
		}
		vboMap.put(location, instanceVbo);
		
		unbind();
	}
	
	@Override
	public void cleanup() {
		super.cleanup();
		if (this.instanceDataBuffer != null) {
			MemoryUtil.memFree(this.instanceDataBuffer);
			this.instanceDataBuffer = null;
		}
	}
	
}
