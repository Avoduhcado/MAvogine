package com.avogine.render.opengl.model.mesh;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

import java.nio.Buffer;
import java.util.function.Supplier;

import org.lwjgl.opengl.*;

import com.avogine.render.model.mesh.Instanceable;
import com.avogine.render.model.mesh.data.*;
import com.avogine.render.opengl.*;
import com.avogine.render.opengl.model.mesh.data.*;

/**
 * 
 */
public class InstancedMesh extends Mesh implements Instanceable {
	
	private int maxInstances;
	
	/**
	 * @param meshData
	 * @param instancedData 
	 */
	public InstancedMesh(MeshData meshData, InstancedData instancedData) {
		super(meshData.aabb(), meshData.getVertexCount(), initVAO(meshData, instancedData));
		this.maxInstances = instancedData.maxInstances();
	}
	
	private static VAO initVAO(MeshData meshData, InstancedData instancedData) {
		try (VertexBuffers vertexBuffers = meshData.vertexBuffers();
				InstancedBuffers instancedBuffers = instancedData.instancedBuffers();) {
			if (vertexBuffers instanceof VertexBuffers(var positions, var normals, var tangents, var bitangents, var textureCoordinates, var c, var w, var b, var indices) &&
					instancedBuffers instanceof InstancedBuffers(var iMatrices, var iNormals)) {
				var vertexAttrib3f = VertexAttrib.Pointer.tightlyPackedUnnormalizedFloat(3);

				return VAO.gen(
						new InstancedVBOBuilder<>(positions, 0, vertexAttrib3f, 0), 
						new InstancedVBOBuilder<>(normals, 1, vertexAttrib3f, 0),
						new InstancedVBOBuilder<>(tangents, 2, vertexAttrib3f, 0),
						new InstancedVBOBuilder<>(bitangents, 3, vertexAttrib3f, 0),
						new InstancedVBOBuilder<>(textureCoordinates, 4, VertexAttrib.Pointer.tightlyPackedUnnormalizedFloat(2), 0),
						() -> {
							var vbo = VBO.gen().bind().bufferData(iMatrices);
							VertexAttrib.array(5).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 0)).divisor(1).enable();
							VertexAttrib.array(6).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 1L * (Float.BYTES * 4))).divisor(1).enable();
							VertexAttrib.array(7).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 2L * (Float.BYTES * 4))).divisor(1).enable();
							VertexAttrib.array(8).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 3L * (Float.BYTES * 4))).divisor(1).enable();
							return vbo;
						},
						() -> {
							var vbo = VBO.gen().bind().bufferData(iNormals);
							VertexAttrib.array(9).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 0)).divisor(1).enable();
							VertexAttrib.array(10).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 1L * (Float.BYTES * 4))).divisor(1).enable();
							VertexAttrib.array(11).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 2L * (Float.BYTES * 4))).divisor(1).enable();
							VertexAttrib.array(12).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 3L * (Float.BYTES * 4))).divisor(1).enable();
							return vbo;
						},
						() -> VBO.genEBO().bind().bufferData(indices));
			} else {
				throw new IllegalArgumentException("Record deconstruction failed");
			}
		} finally {
			VAO.unbind();
		}
	}
	
	private static record InstancedVBOBuilder<T extends Buffer>(T data, int vertexAttribArray, VertexAttrib.Pointer pointer, int divisor) implements Supplier<VBO> {
		@Override
		public VBO get() {
			var vbo = VBO.gen().bind().bufferData(data);
			VertexAttrib.array(vertexAttribArray).pointer(pointer).divisor(divisor).enable();
			return vbo;
		}
	}

	@Override
	public void draw() {
		bind();
		GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, getVertexCount(), GL11.GL_UNSIGNED_INT, 0, maxInstances);
	}

	/**
	 * @param <U>
	 * @param vboIndex
	 * @param offset
	 * @param buffer
	 */
	public <U extends Buffer> void updateInstanceBuffer(int vboIndex, long offset, U buffer) {
		getVAO().vertexBuffers().get(vboIndex).bind().bufferSubData(offset, buffer);
	}
	
	@Override
	public <U extends Buffer> void updateInstanceBuffer(int vboIndex, U buffer) {
		updateInstanceBuffer(vboIndex, 0, buffer);
	}

	@Override
	public int getMaxInstances() {
		return maxInstances;
	}
	
}
