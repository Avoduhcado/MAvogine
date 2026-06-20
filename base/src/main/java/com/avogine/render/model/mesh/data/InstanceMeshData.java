package com.avogine.render.model.mesh.data;

import java.util.*;

/**
 *
 * @param meshData 
 * @param instanceTransforms
 * @param maxInstances
 */
public record InstanceMeshData(MeshData meshData, float[] instanceTransforms, int maxInstances) {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(instanceTransforms);
		result = prime * result + Objects.hash(maxInstances, meshData);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof InstanceMeshData))
			return false;
		InstanceMeshData other = (InstanceMeshData) obj;
		return Arrays.equals(instanceTransforms, other.instanceTransforms) && maxInstances == other.maxInstances
				&& Objects.equals(meshData, other.meshData);
	}

	@Override
	public String toString() {
		return "InstanceMeshData [meshData=" + meshData + ", instanceTransforms=" + instanceTransforms
				+ ", maxInstances=" + maxInstances + "]";
	}
	
}
