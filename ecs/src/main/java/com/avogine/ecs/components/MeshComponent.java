package com.avogine.ecs.components;

import java.util.Arrays;

import com.avogine.ecs.EntityComponent;
import com.avogine.render.data.mesh.StaticMesh;

/**
 * @param meshIndices an array of {@link StaticMesh} IDs used in rendering this component.
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(meshIndices);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MeshComponent other = (MeshComponent) obj;
		if (!Arrays.equals(meshIndices, other.meshIndices))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "MeshComponent [meshIndices=" + Arrays.toString(meshIndices) + "]";
	}

}
