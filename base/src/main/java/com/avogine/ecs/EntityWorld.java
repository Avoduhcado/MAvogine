package com.avogine.ecs;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * TODO Document process of adding entities here, how they're stored and updated, and how to access them through component queries
 * Rename this to EntityManager? For Component/System naming scheme
 */
public class EntityWorld {

	private final AtomicLong entityIDCount;
	
	private final Set<EntityChunk> chunks;
	
	private static final EntityArchetype EMPTY_ARCHETYPE = new EntityArchetype();
	
	/**
	 * 
	 */
	public EntityWorld() {
		entityIDCount = new AtomicLong();
		chunks = new HashSet<>();
	}
	
	private long getNewEntityID() {
		// TODO Change entity IDs into some sort of versioned, generated ID system? Not super necessary
		return entityIDCount.incrementAndGet();
	}
	
	/**
	 * Find the {@link EntityChunk} that has a matching archetype to the entity being created, if no such {@code EntityChunk} exists, a new one will be created.
	 * @param entityID
	 * @param archetype
	 * @param componentMap
	 */
	private void storeChunk(long entityID, EntityArchetype archetype, EntityComponentMap componentMap) {
		chunks.stream()
		.filter(chunk -> chunk.getArchetype().equals(archetype))
		.findFirst()
		.orElseGet(() -> {
			var entityChunk = new EntityChunk(archetype);
			chunks.add(entityChunk);
			return entityChunk;
		})
		.addComponentMap(entityID, componentMap);
		
		chunks.removeIf(chunk -> chunk.getComponentMaps().isEmpty());
	}
	
	/**
	 * Create a new Entity ID with no components linked to it.
	 * @return a new Entity ID
	 */
	public long createEntity() {
		long entityID = getNewEntityID();
		storeChunk(entityID, EMPTY_ARCHETYPE, new EntityComponentMap());
		
		return entityID;
	}
	
	/**
	 * @param archetype
	 * @return a new Entity ID with default components specified by the given {@code EntityArchetype}
	 */
	public long createEntityWith(EntityArchetype archetype) {
		long entityID = getNewEntityID();
		var componentMap = new EntityComponentMap();
		archetype.forEach(clazz -> {
			try {
				Constructor<? extends EntityComponent> constructor = clazz.getDeclaredConstructor();
				if (constructor.trySetAccessible()) {
					componentMap.put(clazz, constructor.newInstance());
				}
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException	| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				// TODO This whole method is sus and I'd really prefer to not have reflection baked in to such a central feature...
				e.printStackTrace();
			}
		});
		storeChunk(entityID, archetype, componentMap);
		
		return entityID;
	}
	
	/**
	 * @param components
	 * @return a new Entity ID created with the supplied {@code EntityComponent}s
	 */
	public long createEntityWith(EntityComponent...components) {
		long entityID = getNewEntityID();
		var componentMap = new EntityComponentMap();
		for (EntityComponent component : components) {
			componentMap.put(component.getClass(), component);
		}
		var archetype = EntityArchetype.of(components);
		storeChunk(entityID, archetype, componentMap);
		
		return entityID;
	}

	/**
	 * @param entityID
	 */
	public void removeEntity(long entityID) {
		chunks.stream()
		.filter(chunk -> chunk.containsEntity(entityID))
		.findFirst()
		.ifPresent(chunk -> chunk.removeComponentMap(entityID));
		
		chunks.removeIf(chunk -> chunk.getComponentMaps().isEmpty());
	}
	
	/**
	 * @param entityID
	 * @return
	 * @throws NoSuchElementException if no entity with the given ID exists in this world
	 */
	public EntityComponentMap getEntity(long entityID) {
		return chunks.stream()
		.filter(chunk -> chunk.containsEntity(entityID))
		.findFirst()
		.orElseThrow()
		.getComponentMap(entityID);
	}
	
	/**
	 * @param entityID
	 * @param component
	 */
	public void addComponent(long entityID, EntityComponent component) {
		chunks.stream()
		.filter(chunk -> chunk.containsEntity(entityID))
		.findFirst()
		.ifPresentOrElse(chunk -> {
			EntityComponentMap entityMap = chunk.removeComponentMap(entityID);
			entityMap.put(component.getClass(), component);
			storeChunk(entityID, EntityArchetype.of(entityMap.values().toArray(new EntityComponent[0])), entityMap);
		}, () -> {
			// Not preferred, but this method can be used to store a new entity
			var entityMap = new EntityComponentMap();
			entityMap.put(component.getClass(), component);
			storeChunk(entityID, EntityArchetype.of(entityMap.values().toArray(new EntityComponent[0])), entityMap);
		});
	}
	
	/**
	 * @param entityID
	 * @param component
	 */
	public void removeComponent(long entityID, EntityComponent component) {
		chunks.stream()
		.filter(chunk -> chunk.containsEntity(entityID))
		.findFirst()
		.ifPresent(chunk -> {
			EntityComponentMap entityMap = chunk.removeComponentMap(entityID);
			entityMap.remove(component.getClass());
			storeChunk(entityID, EntityArchetype.of(entityMap.values().toArray(new EntityComponent[0])), entityMap);
		});
	}
	
	/**
	 * @return the chunks
	 */
	public Set<EntityChunk> getChunks() {
		return chunks;
	}
	
}
