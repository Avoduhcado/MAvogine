package com.avogine.ecs;

import java.util.*;

/**
 *
 */
public class EntityComponentMap extends HashMap<Class<? extends EntityComponent>, EntityComponent> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long entityId;
	
	/**
	 * @param <T>
	 * @param clazz
	 * @return the component contained in this map of type {@code clazz} automatically cast to that type
	 * or {@code null} if no such component exists in this map
	 */
	public <T extends EntityComponent> T getAs(Class<T> clazz) {
		return clazz.cast(get(clazz));
	}
	
	/**
	 * @return the entityId
	 */
	public long getEntityId() {
		return entityId;
	}
	
	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(entityId);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EntityComponentMap other = (EntityComponentMap) obj;
		return entityId == other.entityId;
	}
	
	
	
}
