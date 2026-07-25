package com.avogine.render.opengl.model.mesh.data;

import java.util.Set;

/**
 * 
 * @param vertices 
 * @param index 
 */
public record MeshData(Set<Vertex> vertices, Index index) {
	
}
