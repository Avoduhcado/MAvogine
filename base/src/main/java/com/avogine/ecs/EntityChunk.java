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
	 * Remove all components associated with the given ID.
	 * @param id The ID of the entity to remove components for.
	 */
	public void removeComponents(UUID id) {
		int indexToRemove = getIndexFor(id);
		if (indexToRemove == -1) {
			return;
		}
		
		components.values().forEach(componentArray -> {
			if (indexToRemove == MAX_CHUNK_SIZE - 1) {
				componentArray[indexToRemove] = null;
			} else {
				componentArray[indexToRemove] = componentArray[chunkSize - 1];
				componentArray[chunkSize - 1] = null;
			}
		});
		
		if (indexToRemove == MAX_CHUNK_SIZE - 1) {
			entityIndex[indexToRemove] = null;
		} else {
			entityIndex[indexToRemove] = entityIndex[chunkSize - 1];
			entityIndex[chunkSize - 1] = null;
		}
		
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
	 * @return the element contained at the given index in the array of types {@code clazz} cast to the given type.
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
	 * @return the Set of {@link EntityComponent} types that define every entity contained in this chunk.
	 */
	public Set<Class<? extends EntityComponent>> getArchetype() {
		return components.keySet();
	}

	/**
	 * @return the populated size of component arrays in this chunk.
	 */
	public int getChunkSize() {
		return chunkSize;
	}

	/**
	 * @param i the index of the entity in the chunk to retrieve an ID for.
	 * @return the ID of the entity contained in index {@code i}.
	 * @throws ArrayIndexOutOfBoundsException if {@code i} is less than 0 or greater than or equal to the size of the chunk.
	 */
	public UUID getID(int i) throws ArrayIndexOutOfBoundsException {
		if (i < 0 || i >= chunkSize) {
			throw new ArrayIndexOutOfBoundsException(i);
		}
		return entityIndex[i];
	}

}
