package com.avogine.ecs;

import java.util.*;

/**
 *
 */
public class EntityChunk {

	private static final int MAX_CHUNK_SIZE = 512;

	private int chunkSize;
	private final Map<Class<? extends EntityComponent>, EntityComponent[]> components;
	
	private final UUID[] entityIndex;
	
	/**
	 * @param archetype 
	 */
	public EntityChunk(Set<Class<? extends EntityComponent>> archetype) {
		components = new HashMap<>();
		for (Class<? extends EntityComponent> clazz : archetype) {
			components.put(clazz, new EntityComponent[MAX_CHUNK_SIZE]);
		}
		entityIndex = new UUID[MAX_CHUNK_SIZE];
	}
	
	/**
	 * @param id
	 * @param components
	 */
	public void addComponents(UUID id, EntityComponent...components) {
		for (var component : components) {
			this.components.get(component.getClass())[chunkSize] = component;
		}
		entityIndex[chunkSize] = id;
		chunkSize++;
	}
	
	/**
	 * @param id
	 * @param components
	 */
	public void addComponents(UUID id, Set<EntityComponent> components) {
		for (var component : components) {
			this.components.get(component.getClass())[chunkSize] = component;
		}
		entityIndex[chunkSize] = id;
		chunkSize++;
	}
	
	/**
	 * @param id
	 */
	public void removeComponents(UUID id) {
		int indexToRemove = getIndexFor(id);
		if (indexToRemove == -1) {
			return;
		}
		
		components.values().forEach(componentArray -> {
			for (int i = indexToRemove; i < chunkSize; i++) {
				if (i == MAX_CHUNK_SIZE - 1) {
					componentArray[i] = null;
					entityIndex[i] = null;
				}
				componentArray[i] = componentArray[i + 1];
				entityIndex[i] = entityIndex[i + 1];
			}
		});
		chunkSize--;
	}
	
	private int getIndexFor(UUID id) {
		for (int i = 0; i < chunkSize; i++) {
			if (id == entityIndex[i]) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @param id
	 * @return true if this chunk contains the given entity ID.
	 */
	public boolean containsID(UUID id) {
		for (int i = 0; i < chunkSize; i++) {
			if (entityIndex[i] == id) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return true if this chunk has open spots in its components arrays.
	 */
	public boolean hasRoom() {
		return chunkSize < MAX_CHUNK_SIZE;
	}
	
	/**
	 * @param <T>
	 * @param clazz
	 * @param index
	 * @return
	 */
	public <T extends EntityComponent> T getAs(Class<T> clazz, int index) {
		var component = components.get(clazz)[index];
		return clazz.cast(component);
	}
	
	/**
	 * @param query
	 * @param index
	 */
	public void processQuery(EntityQuery query, int index) {
		query.process(components, index);
	}

	/**
	 * @param query
	 */
	public void processWholeChunkQuery(EntityQuery query) {
		for (int i = 0; i < chunkSize; i++) {
			query.process(components, i);
		}
	}
	
	/**
	 * @return
	 */
	public Set<Class<? extends EntityComponent>> getArchetype() {
		return components.keySet();
	}

	/**
	 * @return
	 */
	public int getChunkSize() {
		return chunkSize;
	}

	/**
	 * @param i
	 * @return
	 */
	public UUID getID(int i) {
		return entityIndex[i];
	}
	
}
