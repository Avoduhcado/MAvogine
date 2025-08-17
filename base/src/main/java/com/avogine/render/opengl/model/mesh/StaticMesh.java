package com.avogine.render.opengl.model.mesh;

import static org.lwjgl.opengl.GL11.*;

import java.util.function.Function;

import org.joml.primitives.AABBf;

import com.avogine.render.model.mesh.Boundable;
import com.avogine.render.model.mesh.data.VertexBuffers;
import com.avogine.render.opengl.*;
import com.avogine.render.opengl.VertexArrayObject.VertexAttrib.Format;
import com.avogine.render.opengl.model.mesh.data.MeshData;

/**
 *
 */
public final class StaticMesh extends Mesh implements Boundable {

	private static final Function<MeshData, Builder> STATIC_VAO = meshData -> {
		try (var vertexBuffers = meshData.vertexBuffers();
				var builder = new VertexArrayObject.Builder();) {
			if (vertexBuffers instanceof VertexBuffers(var positions, var normals, var tangents, var bitangents, var textureCoordinates, var c, var w, var b, var indices)) {
				var vertexFormat3f = Format.tightlyPackedUnnormalizedFloat(3);
				var vertexFormat2f = Format.tightlyPackedUnnormalizedFloat(2);
				return builder
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
		}
	};
	
	private AABBf aabb;
	
	/**
	 * @param vertexData
	 */
	public StaticMesh(MeshData vertexData) {
		super(STATIC_VAO.apply(vertexData), vertexData.getVertexCount());
		aabb = vertexData.aabb();
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
