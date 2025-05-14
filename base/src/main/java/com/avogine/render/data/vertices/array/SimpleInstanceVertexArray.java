package com.avogine.render.data.vertices.array;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

import com.avogine.render.data.gl.*;
import com.avogine.render.data.vertices.vertex.InstanceTransformVertex;

/**
 * @param simpleVertex
 * @param instanceTransform 
 */
public record SimpleInstanceVertexArray(SimpleVertexArray simpleVertex, InstanceTransformVertex instanceTransform) implements IndexedVertexArray {

	@Override
	public VAO bind() {
		try {
			var vertexAttrib3f = VertexAttrib.Pointer.tightlyPackedUnnormalizedFloat(3);
			return VAO.gen().bind()
					.addBuffer(VBO.gen().bind().bufferData(simpleVertex.position().positions())
							.enable(VertexAttrib.array(0).pointer(vertexAttrib3f).divisor(0)))
					.addBuffer(VBO.gen().bind().bufferData(simpleVertex.shading().normals())
							.enable(VertexAttrib.array(1).pointer(vertexAttrib3f).divisor(0)))
					.addBuffer(VBO.gen().bind().bufferData(simpleVertex.shading().tangents())
							.enable(VertexAttrib.array(2).pointer(vertexAttrib3f).divisor(0)))
					.addBuffer(VBO.gen().bind().bufferData(simpleVertex.shading().bitangents())
							.enable(VertexAttrib.array(3).pointer(vertexAttrib3f).divisor(0)))
					.addBuffer(VBO.gen().bind().bufferData(simpleVertex.textureCoordinate().textureCoordinates())
							.enable(VertexAttrib.array(4).pointer(VertexAttrib.Pointer.tightlyPackedUnnormalizedFloat(2)).divisor(0)))
					.addBuffer(VBO.gen().bind().bufferData(instanceTransform.instanceMatrices())
							.enable(VertexAttrib.array(5).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 0)).divisor(1))
							.enable(VertexAttrib.array(6).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 1L * (Float.BYTES * 4))).divisor(1))
							.enable(VertexAttrib.array(7).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 2L * (Float.BYTES * 4))).divisor(1))
							.enable(VertexAttrib.array(8).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 3L * (Float.BYTES * 4))).divisor(1)))
					.addBuffer(VBO.gen().bind().bufferData(instanceTransform.instanceNormals())
							.enable(VertexAttrib.array(9).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 0)).divisor(1))
							.enable(VertexAttrib.array(10).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 1L * (Float.BYTES * 4))).divisor(1))
							.enable(VertexAttrib.array(11).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 2L * (Float.BYTES * 4))).divisor(1))
							.enable(VertexAttrib.array(12).pointer(new VertexAttrib.Pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), 3L * (Float.BYTES * 4))).divisor(1)))
					.addBuffer(VBO.genEBO().bind().bufferData(simpleVertex.element().indices()));
		} finally {
			VAO.unbind();
		}
	}

	@Override
	public int vertexCount() {
		return simpleVertex.vertexCount();
	}
}
