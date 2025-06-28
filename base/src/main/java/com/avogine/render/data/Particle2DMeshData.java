package com.avogine.render.data;

/**
 *
 */
public final class Particle2DMeshData extends MeshData {

	private int maxInstances;
	
	/**
	 * @param vertexBuffers
	 */
	public Particle2DMeshData(VertexBuffers vertexBuffers, int maxInstances) {
		super(vertexBuffers, null);
		this.maxInstances = maxInstances;
	}
	
	@Override
	public int getVertexCount() {
		return getVertexBuffers().positions().limit() / 3;
	}
	
	/**
	 * @return the maxInstances
	 */
	public int getMaxInstances() {
		return maxInstances;
	}

}
