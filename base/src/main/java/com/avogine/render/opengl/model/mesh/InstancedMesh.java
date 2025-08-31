package com.avogine.render.opengl.model.mesh;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;

import java.nio.Buffer;
import java.util.List;
import java.util.function.*;

import com.avogine.render.model.mesh.Instanceable;
import com.avogine.render.model.mesh.data.*;
import com.avogine.render.opengl.*;
import com.avogine.render.opengl.VAO.VAOBuilder.VertexAttrib;
import com.avogine.render.opengl.model.mesh.data.MeshData;

/**
 *
 */
public final class InstancedMesh extends Mesh implements Instanceable {

	private static final ObjLongConsumer<VertexAttrib> INSTANCED_ATTRIB_FORMAT = (attrib, pointerOffset) -> {
		attrib.pointer(4, GL_FLOAT, false, 4 * (Float.BYTES * 4), pointerOffset * (Float.BYTES * 4));
		attrib.divisor(1);
	};
	
	private final int maxInstances;
	
	/**
	 * @param meshData 
	 */
	public InstancedMesh(MeshData meshData) {
		super(meshData);
		maxInstances = meshData.maxInstances();
	}
	
	@Override
	protected VAO setupVAO(MeshData meshData) {
		try (VertexBuffers vertexBuffers = meshData.vertexBuffers();
				InstancedBuffers instancedBuffers = meshData.instancedBuffers();) {
			if (vertexBuffers instanceof VertexBuffers(var positions, var normals, var tangents, var bitangents, var textureCoordinates, var c, var w, var b, var indices) &&
					instancedBuffers instanceof InstancedBuffers(var instanceMatrices, var instanceNormals)) {
				var vertexFormat3f = VertexAttrib.Format.tightlyPackedUnnormalizedFloat(3);
				var vertexFormat2f = VertexAttrib.Format.tightlyPackedUnnormalizedFloat(2);

				return VAO.gen(vertexArray -> vertexArray
						.bindBufferData(VBO.staticDraw(), positions)
						.enablePointerDivisor(0, vertexFormat3f, 0)
						.bindBufferData(VBO.staticDraw(), normals)
						.enablePointerDivisor(1, vertexFormat3f, 0)
						.bindBufferData(VBO.staticDraw(), tangents)
						.enablePointerDivisor(2, vertexFormat3f, 0)
						.bindBufferData(VBO.staticDraw(), bitangents)
						.enablePointerDivisor(3, vertexFormat3f, 0)
						.bindBufferData(VBO.staticDraw(), textureCoordinates)
						.enablePointerDivisor(4, vertexFormat2f, 0)
						.bindBufferData(VBO.staticDraw(), instanceMatrices)
						.enable(VertexAttrib.array(5), attrib -> INSTANCED_ATTRIB_FORMAT.accept(attrib, 0L))
						.enable(VertexAttrib.array(6), attrib -> INSTANCED_ATTRIB_FORMAT.accept(attrib, 1L))
						.enable(VertexAttrib.array(7), attrib -> INSTANCED_ATTRIB_FORMAT.accept(attrib, 2L))
						.enable(VertexAttrib.array(8), attrib -> INSTANCED_ATTRIB_FORMAT.accept(attrib, 3L))
						.bindBufferData(VBO.staticDraw(), instanceNormals)
						.enable(VertexAttrib.array(9), attrib -> INSTANCED_ATTRIB_FORMAT.accept(attrib, 0L))
						.enable(VertexAttrib.array(10), attrib -> INSTANCED_ATTRIB_FORMAT.accept(attrib, 1L))
						.enable(VertexAttrib.array(11), attrib -> INSTANCED_ATTRIB_FORMAT.accept(attrib, 2L))
						.enable(VertexAttrib.array(12), attrib -> INSTANCED_ATTRIB_FORMAT.accept(attrib, 3L))
						.bindElements(indices));
			} else {
				throw new IllegalArgumentException("Record deconstruction failed. VertexBuffers or InstanceBuffers not found.");
			}
		}
	}
	
	@Override
	protected void draw() {
		glDrawElementsInstanced(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0, getMaxInstances());
	}
	
	@Override
	public <T extends Buffer> void updateInstanceBuffer(int vboIndex, long offset, T data) {
		VBO instanceBuffer = getVao().vertexBufferObjects()[vboIndex];
		instanceBuffer.bind();
		instanceBuffer.bufferSubData(offset, data);
	}
	
	/**
	 * @param <T>
	 * @param elements
	 * @param action
	 */
	public <T> void update(List<T> elements, ObjIntConsumer<T> action) {
		getVao().bind();
		for (int i = 0; i < elements.size(); i++) {
			action.accept(elements.get(i), i);
		}
	}
	
	@Override
	public int getMaxInstances() {
		return maxInstances;
	}

}
