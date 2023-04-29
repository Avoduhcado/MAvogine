package com.avogine.ecs;

import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;

import com.avogine.logging.AvoLog;

/**
 * TODO
 */
public class EntityChunk implements Serializable {
	private static final long serialVersionUID = 1L;

	private final EntityComponentSet componentSet;
	
	private final Map<UUID, EntityComponentMap> componentsMap = new HashMap<>();
	
	/**
	 * @param componentSet 
	 * 
	 */
	public EntityChunk(EntityComponentSet componentSet) {
		this.componentSet = componentSet;
	}
	
	/**
	 * @return the componentMaps
	 */
	public Map<UUID, EntityComponentMap> getComponentMaps() {
		return componentsMap;
	}
	
	/**
	 * @param <T>
	 * @param archetype
	 * @return
	 */
	public <T extends Record & EntityArchetype> Stream<T> getComponentsAs(Class<T> archetype) {
		return componentsMap.entrySet().stream()
				.map(entry -> convertToArchetype(entry, archetype))
				.filter(Objects::nonNull);
	}
	
	private <T extends Record & EntityArchetype> T convertToArchetype(Map.Entry<UUID, EntityComponentMap> entity, Class<T> archetype) {
		try {
			Class<?>[] paramTypes = Arrays.stream(archetype.getRecordComponents())
					.map(RecordComponent::getType)
					.toArray(Class<?>[]::new);
			var archetypeConstructor = archetype.getDeclaredConstructor(paramTypes);
			
			Object[] params = Arrays.stream(paramTypes)
					.map(type -> mapTypeFromComponentMap(type, entity))
					.toArray();
			archetypeConstructor.setAccessible(true);
			return archetypeConstructor.newInstance(params);
		} catch (NoSuchMethodException e) {
			AvoLog.log().error("Failed to find EntityArchetype constructor.", e);
			return null;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			AvoLog.log().error("Failed to build instance of EntityArchetype.", e);
			return null;
		}
	}
	
	private Object mapTypeFromComponentMap(Class<?> type, Map.Entry<UUID, EntityComponentMap> entity) {
		if (EntityComponent.class.isAssignableFrom(type)) {
			return entity.getValue().get(type);
		} else if (UUID.class.isAssignableFrom(type)) {
			return entity.getKey();
		} else {
			AvoLog.log().warn("Found an archetype param that isn't an EntityComponent or UUID: {}", type);
			return null;
		}
	}
	
	/**
	 * @param entity
	 * @return
	 */
	public EntityComponentMap getComponentMap(UUID entity) {
		return componentsMap.get(entity);
	}
	
	/**
	 * 
	 * @param entityID 
	 * @param componentMap
	 */
	public void addComponentMap(UUID entityID, EntityComponentMap componentMap) {
		componentsMap.put(entityID, componentMap);
	}
	
	/**
	 * 
	 * @param entityID
	 * @return
	 */
	public EntityComponentMap removeComponentMap(UUID entityID) {
		return componentsMap.remove(entityID);
	}
	
	/**
	 * TODO
	 * @param <T>
	 * @param archetype
	 * @return
	 */
	public <T extends Record & EntityArchetype> boolean containsAll(Class<T> archetype) {
		var components = Arrays.stream(archetype.getRecordComponents())
				.map(RecordComponent::getType)
				.filter(clazz -> EntityComponent.class.isAssignableFrom(clazz))
				.toList();
		return this.componentSet.containsAll(components);
	}
	
	/**
	 * @param entityID the Long ID of an entity
	 * @return true if this chunk contains the given entity ID
	 */
	public boolean containsEntity(UUID entityID) {
		return componentsMap.containsKey(entityID);
	}
	
	/**
	 * @return the archetype
	 */
	public EntityComponentSet getComponentSet() {
		return componentSet;
	}
	
}
