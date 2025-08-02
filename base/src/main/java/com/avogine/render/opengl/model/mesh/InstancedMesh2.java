package com.avogine.render.opengl.model.mesh;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;

import java.nio.Buffer;

import com.avogine.render.model.mesh.Instanceable;
import com.avogine.render.model.mesh.data.*;
import com.avogine.render.opengl.model.mesh.data.InstancedMeshData;

/**
 *
 */
public final class InstancedMesh2 extends MaterialMesh<InstancedMeshData> implements Instanceable {

	private final int maxInstances;
	
	/**
	 * @param vertexData
	 */
	public InstancedMesh2(InstancedMeshData vertexData) {
		super(vertexData);
		maxInstances = vertexData.maxInstances();
	}

	@Override
	protected Builder init(InstancedMeshData vertexData) {
		try (VertexBuffers vertexBuffers = vertexData.meshData().vertexBuffers();
				InstancedBuffers instancedBuffers = vertexData.instancedBuffers()) {
			if (vertexBuffers instanceof VertexBuffers(var positions, var normals, var tangents, var bitangents, var textureCoordinates, var c, var w, var b, var indices) &&
					instancedBuffers instanceof InstancedBuffers(var instanceMatrices, var instanceNormals)) {
				var vertexFormat3f = VertexAttrib.Format.tightlyPackedUnnormalizedFloat(3);
				var vertexFormat2f = VertexAttrib.Format.tightlyPackedUnnormalizedFloat(2);
				
				return new Builder()
						.buffer(VertexBufferObject.arrayBufferStaticDraw(positions))
						.attrib(VertexAttrib.array(0).pointer(vertexFormat3f).divisor(0))
						.buffer(VertexBufferObject.arrayBufferStaticDraw(normals))
						.attrib(VertexAttrib.array(1).pointer(vertexFormat3f).divisor(0))
						.buffer(VertexBufferObject.arrayBufferStaticDraw(tangents))
						.attrib(VertexAttrib.array(2).pointer(vertexFormat3f).divisor(0))
						.buffer(VertexBufferObject.arrayBufferStaticDraw(bitangents))
						.attrib(VertexAttrib.array(3).pointer(vertexFormat3f).divisor(0))
						.buffer(VertexBufferObject.arrayBufferStaticDraw(textureCoordinates))
						.attrib(VertexAttrib.array(4).pointer(vertexFormat2f).divisor(0))
						.buffer(VertexBufferObject.arrayBufferStaticDraw(instanceMatrices))
						.attrib(VertexAttrib.array(5).pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 0).divisor(1))
						.attrib(VertexAttrib.array(6).pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 1L * (Float.BYTES * 4)).divisor(1))
						.attrib(VertexAttrib.array(7).pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 2L * (Float.BYTES * 4)).divisor(1))
						.attrib(VertexAttrib.array(8).pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 3L * (Float.BYTES * 4)).divisor(1))
						.buffer(VertexBufferObject.arrayBufferStaticDraw(instanceNormals))
						.attrib(VertexAttrib.array(9).pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 0).divisor(1))
						.attrib(VertexAttrib.array(10).pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 1L * (Float.BYTES * 4)).divisor(1))
						.attrib(VertexAttrib.array(11).pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 2L * (Float.BYTES * 4)).divisor(1))
						.attrib(VertexAttrib.array(12).pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 3L * (Float.BYTES * 4)).divisor(1))
						.buffer(VertexBufferObject.elementBuffer(indices));
			} else {
				throw new IllegalArgumentException("Record deconstruction failed.");
			}
		} finally {
			unbind();
		}
	}

	@Override
	public void draw() {
		glDrawElementsInstanced(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0, getMaxInstances());
	}
	
	@Override
	public <T extends Buffer> void updateInstanceBuffer(int vboIndex, long offset, T data) {
		getVertexBufferObjects()[vboIndex].bind().bufferSubData(offset, data);
	}
	
	@Override
	public int getMaxInstances() {
		return maxInstances;
	}

}
