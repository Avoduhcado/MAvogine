package com.avogine.ecs;

import java.util.*;
import java.util.stream.Stream;

/**
 * TODO Document process of adding entities here, how they're stored and updated, and how to access them through component queries
 */
public class EntityManager {

	private final Set<EntityChunk> chunks;
	
	private final Map<Class<? extends EntitySystemAddon>, EntitySystemAddon> addons;
	
	/**
	 * 
	 */
	public EntityManager() {
		chunks = new HashSet<>();
		addons = new HashMap<>();
	}
	
	private UUID getNewEntityID() {
		return UUID.randomUUID();
	}
	
	/**
	 * Create a new Entity ID with no components linked to it.
	 * @return a new Entity ID
	 */
	public UUID createEntity() {
		return createEntityWith();
	}
	
	/**
	 * @param components
	 * @return a new Entity ID created with the supplied {@code EntityComponent}s
	 */
	public UUID createEntityWith(EntityComponent...components) {
		UUID entityID = getNewEntityID();
		var componentMap = new EntityComponentMap();
		for (EntityComponent component : components) {
			componentMap.put(component.getClass(), component);
		}
		var componentSet = EntityComponentSet.of(components);
		storeChunk(entityID, componentSet, componentMap);
		
		return entityID;
	}

	/**
	 * @param entityID The ID of the entity to retrieve.
	 * @return The {@link EntityComponentMap} matching the given ID or null if no entity exists with that ID.
	 */
	public Optional<EntityComponentMap> getEntity(UUID entityID) {
		return chunks.stream()
				.flatMap(chunk -> chunk.getComponentMaps().entrySet().stream())
				.filter(entry -> entry.getKey().equals(entityID))
				.findFirst()
				.map(map -> map.getValue());
	}
	
	/**
	 * @param entityID
	 */
	public void removeEntity(UUID entityID) {
		chunks.stream()
		.filter(chunk -> chunk.containsEntity(entityID))
		.findFirst()
		.ifPresent(chunk -> chunk.removeComponentMap(entityID, true));
		
		removeEmptyChunks();
	}
	
	/**
	 * @param <T>
	 * @param archetype
	 * @return
	 */
	public <T extends Record & EntityArchetype> Stream<T> query(Class<T> archetype) {
		return chunks.stream()
				.filter(chunk -> chunk.containsAll(archetype))
				.flatMap(chunk -> chunk.getComponentsAs(archetype));
	}
	
	/**
	 * @param entityID
	 * @param component
	 */
	public void addComponent(UUID entityID, EntityComponent component) {
		chunks.stream()
		.filter(chunk -> chunk.containsEntity(entityID))
		.findFirst()
		.ifPresentOrElse(chunk -> {
			EntityComponentMap entityMap = chunk.removeComponentMap(entityID);
			entityMap.put(component.getClass(), component);
			storeChunk(entityID, EntityComponentSet.of(entityMap.values().toArray(new EntityComponent[0])), entityMap);
		}, () -> {
			// Not preferred, but this method can be used to store a new entity
			var entityMap = new EntityComponentMap();
			entityMap.put(component.getClass(), component);
			storeChunk(entityID, EntityComponentSet.of(entityMap.values().toArray(new EntityComponent[0])), entityMap);
		});
	}
	
	/**
	 * @param entityID
	 * @param component
	 */
	public void removeComponent(UUID entityID, EntityComponent component) {
		chunks.stream()
		.filter(chunk -> chunk.containsEntity(entityID))
		.findFirst()
		.ifPresent(chunk -> {
			EntityComponentMap entityMap = chunk.removeComponentMap(entityID);
			entityMap.remove(component.getClass());
			storeChunk(entityID, EntityComponentSet.of(entityMap.values().toArray(new EntityComponent[0])), entityMap);
		});
	}

	/**
	 * Find the {@link EntityChunk} that has a matching archetype to the entity being created, if no such {@code EntityChunk} exists, a new one will be created.
	 * @param entityID
	 * @param componentSet
	 * @param componentMap
	 */
	private void storeChunk(UUID entityID, EntityComponentSet componentSet, EntityComponentMap componentMap) {
		chunks.stream()
		.filter(chunk -> chunk.getComponentSet().equals(componentSet))
		.findFirst()
		.orElseGet(() -> {
			var entityChunk = new EntityChunk(componentSet);
			chunks.add(entityChunk);
			return entityChunk;
		})
		.addComponentMap(entityID, componentMap);

		removeEmptyChunks();
	}
	
	private void removeEmptyChunks() {
		chunks.removeIf(chunk -> chunk.getComponentMaps().isEmpty());
	}
	
	/**
	 * @return the chunks
	 */
	public Set<EntityChunk> getChunks() {
		return chunks;
	}

	/**
	 * Register an {@link EntitySystemAddon} to this {@link EntityManager}.
	 * 
	 * <p>{@code EntitySystemAddon}s can contain arbitrary data useful to managing {@link EntitySystem}s. If an {@code EntitySystem}
	 * would otherwise need to contain data to manage processing, that data should be relocated into an
	 * {@code EntitySystemAddon}. {@code EntitySystemAddon}s will be automatically serialized when saving the
	 * game state, and can contain global data that would not otherwise make sense to be stored in individual
	 * components, ie. shared data.
	 * @param addon the {@code EntitySystemAddon} to register
	 * @return the previous value associated with the given addon's class, or null if there was no mapping for that class.
	 */
	public EntitySystemAddon registerAddon(EntitySystemAddon addon) {
		return this.addons.put(addon.getClass(), addon);
	}
	
	/**
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	public <T extends EntitySystemAddon> Optional<T> getAddon(Class<T> clazz) {
		EntitySystemAddon addon = addons.get(clazz);
		if (addon != null && addon.getClass().isAssignableFrom(clazz)) {
			return Optional.of(clazz.cast(addon));
		}
		return Optional.empty();
	}

	/**
	 * @return the addons
	 */
	public Map<Class<? extends EntitySystemAddon>, EntitySystemAddon> getAddons() {
		return addons;
	}
	
}
