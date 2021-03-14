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
	
	private final Map<Long, EntityChunk> entityMap;
	
	private Set<EntityChunk> chunks = new HashSet<>();
	
	/**
	 * 
	 */
	public EntityWorld() {
		entityIDCount = new AtomicLong();
		entityMap = new HashMap<>();
	}
	
	private long getNewEntityID() {
		// TODO Change entity IDs into some sort of versioned, generated ID system? Not super necessary
		return entityIDCount.incrementAndGet();
	}
	
	/**
	 * Find the {@link EntityChunk} that has a matching archetype to the entity being created, if no such {@code EntityChunk} exists, a new one will be created.
	 * @param entityID
	 * @param archetype
	 */
	private void storeChunk(long entityID, EntityArchetype archetype, EntityComponentMap componentMap) {
		Optional<EntityChunk> targetChunk = chunks.stream()
				.filter(chunk -> chunk.getArchetype().equals(archetype))
				.findFirst();
		chunks.add(entityMap.computeIfAbsent(entityID, value -> {
			EntityChunk chunkForEntity = targetChunk.orElseGet(() -> new EntityChunk(archetype));
			chunkForEntity.addComponentMap(entityID, componentMap);
			return chunkForEntity;
		}));
	}
	
	/**
	 * Create a new Entity ID with no components linked to it.
	 * @return a new Entity ID
	 */
	public long createEntity() {
		long entityID = getNewEntityID();
		entityMap.computeIfAbsent(entityID, value -> null);
		return entityID;
	}
	
	/**
	 * @param archetype
	 * @return a new Entity ID with default components specified by the given {@code EntityArchetype}
	 */
	public long createEntityWith(EntityArchetype archetype) {
		long entityID = getNewEntityID();
		EntityComponentMap componentMap = new EntityComponentMap();
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
		EntityComponentMap componentMap = new EntityComponentMap();
		for (EntityComponent component : components) {
			componentMap.put(component.getClass(), component);
		}
		EntityArchetype archetype = EntityArchetype.of(components);
		storeChunk(entityID, archetype, componentMap);
		
		return entityID;
	}
	
	/**
	 * @return the chunks
	 */
	public Set<EntityChunk> getChunks() {
		return chunks;
	}
	
}
