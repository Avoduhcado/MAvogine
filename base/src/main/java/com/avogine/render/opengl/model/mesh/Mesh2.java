package com.avogine.render.opengl.model.mesh;

import static org.lwjgl.opengl.GL11.*;

import org.joml.primitives.AABBf;

import com.avogine.render.model.mesh.Boundable;
import com.avogine.render.model.mesh.data.VertexBuffers;
import com.avogine.render.opengl.model.mesh.data.MeshData;

/**
 *
 */
public final class Mesh2 extends MaterialMesh<MeshData> implements Boundable {

	private AABBf aabb;
	
	/**
	 * @param vertexData
	 */
	public Mesh2(MeshData vertexData) {
		super(vertexData);
		aabb = vertexData.aabb();
	}
		@Override
	protected Builder init(MeshData vertexData) {
		try (VertexBuffers vertexBuffers = vertexData.vertexBuffers()) {
			if (vertexBuffers instanceof VertexBuffers(var positions, var normals, var tangents, var bitangents, var textureCoordinates, var c, var w, var b, var indices)) {
				var vertexFormat3f = VertexAttrib.Format.tightlyPackedUnnormalizedFloat(3);
				var vertexFormat2f = VertexAttrib.Format.tightlyPackedUnnormalizedFloat(2);
				
				return new Builder()
						.buffer(VertexBufferObject.arrayBufferStaticDraw(positions))
						.attrib(VertexAttrib.array(0).pointer(vertexFormat3f))
						.buffer(VertexBufferObject.arrayBufferStaticDraw(normals))
						.attrib(VertexAttrib.array(1).pointer(vertexFormat3f))
						.buffer(VertexBufferObject.arrayBufferStaticDraw(tangents))
						.attrib(VertexAttrib.array(2).pointer(vertexFormat3f))
						.buffer(VertexBufferObject.arrayBufferStaticDraw(bitangents))
						.attrib(VertexAttrib.array(3).pointer(vertexFormat3f))
						.buffer(VertexBufferObject.arrayBufferStaticDraw(textureCoordinates))
						.attrib(VertexAttrib.array(4).pointer(vertexFormat2f))
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
		glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
	}

	@Override
	public AABBf getAABB() {
		return aabb;
	}

}
