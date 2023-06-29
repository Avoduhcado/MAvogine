package com.avogine.ecs;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.RecordComponent;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.avogine.ecs.components.*;

/**
 *
 */
public class ECSChunkTest {

	@Test
	void testChunkQuery() {
		Set<Class<? extends EntityComponent>> componentSet = new HashSet<>(List.of(TransformComponent.class, ModelComponent.class, AudioComponent.class));
		Map<Class<? extends EntityComponent>, Map<UUID, EntityComponent>> chunkMap = componentSet.stream().collect(Collectors.toUnmodifiableMap(Function.identity(), v -> new HashMap<UUID, EntityComponent>()));
		
		UUID id = UUID.randomUUID();
		EntityComponent[] entity = { new TransformComponent(), new ModelComponent(""), new AudioComponent(new ArrayList<>()) };
		
		for (EntityComponent c : entity) {
			chunkMap.get(c.getClass()).put(id, c);
		}
		
		assertEquals(3, chunkMap.entrySet().size());
		for (Class<? extends EntityComponent> clazz : chunkMap.keySet()) {
			assertEquals(1, chunkMap.get(clazz).keySet().size());
		}
		
		record Renderable(TransformComponent t, ModelComponent m) {}
		var components = getArchetypeComponents(Renderable.class);
		
		chunkMap.entrySet().stream()
		.filter(entry -> components.contains(entry.getKey()))
		.flatMap(entry -> entry.getValue().entrySet().stream());
	}
	
	private List<?> getArchetypeComponents(Class<?> archetype) {
		return Arrays.stream(archetype.getRecordComponents())
				.map(RecordComponent::getType)
				.filter(clazz -> EntityComponent.class.isAssignableFrom(clazz))
				.toList();
	}
	
	@Test
	void testComponentHash() {
		Set<Class<? extends EntityComponent>> archetypeA = new HashSet<>(List.of(TransformComponent.class, ModelComponent.class, AudioComponent.class));
		Set<Class<? extends EntityComponent>> archetypeB = new HashSet<>(List.of(ModelComponent.class, TransformComponent.class, AudioComponent.class));
		
		assertTrue(archetypeA.hashCode() == archetypeB.hashCode());
		
		Set<Class<? extends EntityComponent>> archetypeC = new HashSet<>(List.of(TransformComponent.class, ModelComponent.class, AudioComponent.class));
		Set<Class<? extends EntityComponent>> archetypeD = new HashSet<>(List.of(ModelComponent.class, AudioComponent.class));
		
		assertTrue(archetypeC.hashCode() != archetypeD.hashCode());
		
		Set<Class<? extends EntityComponent>> archetypeA1 = Set.of(TransformComponent.class, ModelComponent.class, AudioComponent.class);
		Set<Class<? extends EntityComponent>> archetypeB1 = Set.of(ModelComponent.class, TransformComponent.class, AudioComponent.class);
		
		assertTrue(archetypeA1.hashCode() == archetypeB1.hashCode());
		
		Set<Class<? extends EntityComponent>> archetypeC1 = Set.of(TransformComponent.class, ModelComponent.class, AudioComponent.class);
		Set<Class<? extends EntityComponent>> archetypeD1 = Set.of(ModelComponent.class, AudioComponent.class);
		
		assertTrue(archetypeC1.hashCode() != archetypeD1.hashCode());
	}
	
	@Test
	void testGetAsFrom() {
		Set<Class<? extends EntityComponent>> archetypeA = Set.of(TransformComponent.class, ModelComponent.class, AudioComponent.class);
		Set<Class<? extends EntityComponent>> archetypeB = Set.of(ModelComponent.class, AudioComponent.class);
		Set<Class<? extends EntityComponent>> archetypeC = Set.of(TransformComponent.class, ModelComponent.class);
		
		var chunkA = new EntityChunk(archetypeA);
		var chunkB = new EntityChunk(archetypeB);
		var chunkC = new EntityChunk(archetypeC);
		
		Set<UUID> aIDs = new HashSet<>();
		for (int i = 0; i < 50; i++) {
			Set<EntityComponent> components = Set.of(new TransformComponent(), new ModelComponent(""), new AudioComponent(new ArrayList<>()));
			var id = UUID.randomUUID();
			aIDs.add(id);
			chunkA.addComponents(id, components);
		}

		Set<UUID> bIDs = new HashSet<>();
		for (int i = 0; i < 15; i++) {
			Set<EntityComponent> components = Set.of(new ModelComponent(""), new AudioComponent(new ArrayList<>()));
			var id = UUID.randomUUID();
			bIDs.add(id);
			chunkB.addComponents(id, components);
		}

		Set<UUID> cIDs = new HashSet<>();
		for (int i = 0; i < 75; i++) {
			Set<EntityComponent> components = Set.of(new TransformComponent(), new ModelComponent(""));
			var id = UUID.randomUUID();
			cIDs.add(id);
			chunkC.addComponents(id, components);
		}
		
		List<EntityChunk> chunks = List.of(chunkA, chunkB, chunkC);
		
		var queriedEntitySize = chunks.stream()
			.filter(chunk -> chunk.getArchetype().containsAll(Set.of(TransformComponent.class, ModelComponent.class)))
			.mapToLong(chunk -> chunk.getChunkSize())
			.sum();
		assertEquals(125, queriedEntitySize);
		
		chunks.stream()
		.filter(chunk -> chunk.getArchetype().containsAll(Set.of(TransformComponent.class, ModelComponent.class)))
		.forEach(chunk -> {
			for (int i = 0; i < chunk.getChunkSize(); i++) {
				assertNotNull(chunk.getAs(TransformComponent.class, i));
				assertNotNull(chunk.getAs(ModelComponent.class, i));
				if (aIDs.contains(chunk.getID(i))) {
					assertNotNull(chunk.getAs(AudioComponent.class, i));
				} else if (cIDs.contains(chunk.getID(i))) {
					try {
						chunk.getAs(AudioComponent.class, i);
					} catch (Exception e) {
						assertInstanceOf(NullPointerException.class, e);
					}
				}
				assertFalse(bIDs.contains(chunk.getID(i)));
			}
		});
	}
	
}
