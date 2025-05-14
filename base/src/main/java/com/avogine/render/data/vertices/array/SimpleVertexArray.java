package com.avogine.render.data.vertices.array;

import com.avogine.render.data.gl.*;
import com.avogine.render.data.vertices.vertex.*;

/**
 * Data wrapper for a list of vertices containing position, normal, tangent, bi-tangent and UV coordinate data and associated element indices.
 * @param position 
 * @param shading 
 * @param textureCoordinate 
 * @param element 
 */
public record SimpleVertexArray(PositionVertex position, ShadingVertex shading, TextureCoordinateVertex textureCoordinate, ElementVertex element) implements IndexedVertexArray {

	@Override
	public VAO bind() {
		try {
			var vertexAttrib3f = VertexAttrib.Pointer.tightlyPackedUnnormalizedFloat(3);
			return VAO.gen().bind()
					.addBuffer(VBO.gen().bind()
							.bufferData(position.positions()).enable(VertexAttrib.array(0).pointer(vertexAttrib3f)))
					.addBuffer(VBO.gen().bind()
							.bufferData(shading.normals()).enable(VertexAttrib.array(1).pointer(vertexAttrib3f)))
					.addBuffer(VBO.gen().bind()
							.bufferData(shading.tangents()).enable(VertexAttrib.array(2).pointer(vertexAttrib3f)))
					.addBuffer(VBO.gen().bind()
							.bufferData(shading.bitangents()).enable(VertexAttrib.array(3).pointer(vertexAttrib3f)))
					.addBuffer(VBO.gen().bind()
							.bufferData(textureCoordinate.textureCoordinates()).enable(VertexAttrib.array(4).pointer(VertexAttrib.Pointer.tightlyPackedUnnormalizedFloat(2))))
					.addBuffer(VBO.genEBO().bind()
							.bufferData(element.indices()));
		} finally {
			VAO.unbind();
		}
	}

	@Override
	public int vertexCount() {
		return element.indices().limit();
	}

}
