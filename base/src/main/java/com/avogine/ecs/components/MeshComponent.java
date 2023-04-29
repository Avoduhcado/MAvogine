package com.avogine.ecs.components;

import com.avogine.ecs.EntityComponent;
import com.avogine.render.data.mesh.Mesh;

/**
 * @param meshIndices an array of {@link Mesh} IDs used in rendering this component.
 */
public record MeshComponent(int[] meshIndices) implements EntityComponent {
	
	/**
	 * 
	 */
	public MeshComponent() {
		this(new int[0]);
	}
	
	/**
	 * 
	 * @param meshIndex
	 */
	public MeshComponent(int meshIndex) {
		this(new int[] {meshIndex});
	}
}
