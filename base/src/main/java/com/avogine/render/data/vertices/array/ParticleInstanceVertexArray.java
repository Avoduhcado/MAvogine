package com.avogine.render.data.vertices.array;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;

import com.avogine.render.data.gl.*;
import com.avogine.render.data.gl.VertexAttrib.Pointer;
import com.avogine.render.data.vertices.vertex.PositionVertex;

/**
 * @param positions 
 * @param instancedPositionSize 
 * @param instancedColorSize 
 * @param maxInstances 
 */
public record ParticleInstanceVertexArray(PositionVertex positions, long instancedPositionSize, long instancedColorSize, int maxInstances) implements IndexedVertexArray {

	@Override
	public VAO bind() {
		try {
			return VAO.gen().bind()
					.addBuffer(VBO.gen().bind()
							.bufferData(positions.positions())
							.enable(VertexAttrib.array(0)
									.pointer(Pointer.tightlyPackedUnnormalizedFloat(3))
									.divisor(0)))
					.addBuffer(VBO.gen().bind()
							.bufferData(instancedPositionSize * maxInstances, GL_STREAM_DRAW)
							.enable(VertexAttrib.array(1)
									.pointer(Pointer.tightlyPackedUnnormalizedFloat(4))
									.divisor(1)))
					.addBuffer(VBO.gen().bind()
							.bufferData(instancedColorSize * maxInstances, GL_STREAM_DRAW)
							.enable(VertexAttrib.array(2)
									.pointer(new Pointer(4, GL_UNSIGNED_BYTE, true, 0, 0))
									.divisor(1)));
		} finally {
			VAO.unbind();
		}
	}

	@Override
	public int vertexCount() {
		return positions.positions().limit() / 3;
	}

}
