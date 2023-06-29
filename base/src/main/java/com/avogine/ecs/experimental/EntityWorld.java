package com.avogine.ecs.experimental;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.*;

/**
 *
 */
public class EntityWorld {

	private List<EntityChunk> chunks;
	private List<EntityChunkList> listChunks;
	
	/**
	 * 
	 */
	public EntityWorld() {
		chunks = new ArrayList<>();
		listChunks = new ArrayList<>();
	}
	
	public Stream<EntityChunk> queryChunks(Set<Class<? extends EntityComponent>> archetype) {
		return chunks.stream().filter(chunk -> chunk.getArchetype().containsAll(archetype));
	}
	
	/**
	 * @param classes
	 * @return
	 */
	@SafeVarargs
	public final Stream<EntityChunk> queryChunks(Class<? extends EntityComponent>...classes) {
		var archetype = Set.of(classes);
		return queryChunks(archetype);
	}
	
	public Stream<Entry<UUID, Map<Class<? extends EntityComponent>, EntityComponent>>> queryLists(Set<Class<? extends EntityComponent>> archetype) {
		return listChunks.stream()
				.filter(chunk -> chunk.getArchetype().equals(archetype))
				.flatMap(chunk -> chunk.getComponents().entrySet().stream());
	}
	
	public Stream<Map<Class<? extends EntityComponent>, EntityComponent>> queryListsNoId(Set<Class<? extends EntityComponent>> archetype) {
		return listChunks.stream()
				.filter(chunk -> chunk.getArchetype().equals(archetype))
				.flatMap(chunk -> chunk.getComponents().entrySet().stream())
				.flatMap(entry -> Stream.of(entry.getValue()));
	}
	
	public Stream<EntityChunk> query(EntityQuery query) {
		return chunks.stream().filter(chunk -> chunk.getArchetype().containsAll(query.getParamTypes()));
	}
	
	public void queryAndProcess(EntityQuery query) {
		chunks.stream()
		.filter(chunk -> chunk.getArchetype().containsAll(query.getParamTypes()))
		.forEach(chunk -> chunk.processWholeChunk(query));
	}
	
	@SafeVarargs
	public final void addEntity(EntityComponent...components) {
		Set<Class<? extends EntityComponent>> archetype = Stream.of(components).map(EntityComponent::getClass).collect(Collectors.toSet());
		var matchingChunk = chunks.stream()
		.filter(chunk -> chunk.getArchetype().equals(archetype) && chunk.getChunkSize() < 512)
		.findFirst()
		.orElseGet(() -> {
			var newChunk = new EntityChunk(archetype);
			chunks.add(newChunk);
			return newChunk;
		});
		for (EntityComponent component : components) {
			matchingChunk.getComponents().get(component.getClass())[matchingChunk.getChunkSize()] = component;
		}
		matchingChunk.setChunkSize(matchingChunk.getChunkSize() + 1);
		
		listChunks.stream()
		.filter(chunk -> chunk.getArchetype().equals(archetype))
		.findFirst()
		.orElseGet(() -> {
			var newChunk = new EntityChunkList(archetype);
			listChunks.add(newChunk);
			return newChunk;
		}).putAll(components);
	}
	
	public List<EntityChunk> getChunks() {
		return chunks;
	}
	
	public List<EntityChunkList> getChunkLists() {
		return listChunks;
	}
	
}
