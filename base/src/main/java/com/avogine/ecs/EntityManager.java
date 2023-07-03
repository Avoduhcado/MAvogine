package com.avogine.ecs;

import java.util.*;
import java.util.stream.*;

/**
 * Top level container for Entity Component System.
 * </p>
 * This is the primary entry point for interacting with {@link EntityComponent}s. Each component
 * is stored in an {@link EntityChunk} according to what other {@code EntityComponents} are associated
 * with the given entity ID in a maximum of 512 entities per chunk before a new {@link EntityChunk} is
 * created.
 * </p>
 * Components can be accessed in multiple ways, all starting from a {@code query()} method depending on
 * the desired performance/safety levels of access. You can either directly query for a set of component
 * types by class via {@link #query(Set)} to receive a {@code Stream} of {@code EntityChunks} that are guaranteed
 * to at least contain all specified {@code EntityComponents}.
 * From there it's up to the implementor to access individual components from those chunks via the
 * {@code EntityChunk#getAs(Class<? extends EntityComponent> clazz)} method. This approach makes no guarantee that the type
 * requested is contained in the initial set of query parameters. In a majority of all cases, querying with an implementation 
 * of {@link EntityQuery} should be sufficient instead of the previous approach as it supplies varied functional consumer
 * methods to operate on the queried chunks albeit in a slightly slower fashion.
 */
public class EntityManager {
	
	private final List<EntityChunk> chunks;
	
	/**
	 * 
	 */
	public EntityManager() {
		chunks = new ArrayList<>();
	}
	
	/**
	 * @param components
	 * @return The ID of the added entity.
	 */
	public UUID createEntityWith(EntityComponent...components) {
		var id = UUID.randomUUID();
		return addEntity(id, components);
	}
	
	/**
	 * @param id 
	 * @param components
	 * @return The ID of the added entity.
	 */
	public UUID addEntity(UUID id, EntityComponent...components) {
		Set<Class<? extends EntityComponent>> archetype = Stream.of(components)
				.map(EntityComponent::getClass)
				.collect(Collectors.toSet());
		
		chunks.stream()
		.filter(chunk -> chunk.getArchetype().equals(archetype) && chunk.hasRoom())
		.findFirst()
		.orElseGet(() -> {
			var newChunk = new EntityChunk(archetype);
			chunks.add(newChunk);
			return newChunk;
		}).addComponents(id, components);
		
		return id;
	}
	
	/**
	 * @param id
	 */
	public void removeEntity(UUID id) {
		chunks.stream()
		.filter(chunk -> chunk.containsID(id))
		.findFirst()
		.ifPresent(chunk -> chunk.removeComponents(id));
		
		chunks.removeIf(chunk -> chunk.getChunkSize() == 0);
	}
	
	/**
	 * @param archetype
	 * @return
	 */
	public Stream<EntityChunk> query(Set<Class<? extends EntityComponent>> archetype) {
		return chunks.stream().filter(chunk -> chunk.getArchetype().containsAll(archetype));
	}
	
	/**
	 * @param classes
	 * @return
	 */
	@SafeVarargs
	public final Stream<EntityChunk> query(Class<? extends EntityComponent>...classes) {
		var archetype = Set.of(classes);
		return chunks.stream().filter(chunk -> chunk.getArchetype().containsAll(archetype));
	}
	
	// TODO It may be worth exposing direct query(Class<T> type1, Class<U> type2, BiConsumer<T, U> consumer, int index) methods as they seem to perform faster at the cost of no type safety, for the trustworthy developer
	
	/**
	 * @param query
	 * @return
	 */
	public Stream<EntityChunk> query(EntityQuery query) {
		return chunks.stream().filter(chunk -> chunk.getArchetype().containsAll(query.getParamTypes()));
	}
	
	/**
	 * @param query
	 */
	public void queryAndProcess(EntityQuery query) {
		chunks.stream()
		.filter(chunk -> chunk.getArchetype().containsAll(query.getParamTypes()))
		.forEach(chunk -> chunk.processWholeChunkQuery(query));
	}
	
}
