package com.avogine.entity;

/**
 *
 */
public class Entity {
	
	private final String id;
	private final Transform transform;
	private String modelId;
	
	/**
	 * @param id 
	 * @param modelId 
	 * 
	 */
	public Entity(String id, String modelId) {
		this.id = id;
		transform = new Transform();
		this.setModelId(modelId);
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the transform
	 */
	public Transform getTransform() {
		return transform;
	}

	/**
	 * @return the modelId
	 */
	public String getModelId() {
		return modelId;
	}

	/**
	 * @param modelId the modelId to set
	 */
	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

}
