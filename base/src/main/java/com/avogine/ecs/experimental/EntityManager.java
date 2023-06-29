package com.avogine.ecs.experimental;

import java.lang.reflect.Array;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Stream;

/**
 *
 */
public class EntityManager {

	private final List<EntityIndex> entityIndices;
	private final Map<Class<? extends EntityComponent>, EntityComponent[]> componentMap;
	private final Map<Class<? extends EntityComponent>, List<EntityComponent[]>> scalingComponentMap;
	private final Map<Class<? extends EntityComponent>, List<EntityComponent>> scaledComponentMap;
	private final Map<Set<Class<? extends EntityComponent>>, Map<UUID, Map<Class<? extends EntityComponent>, EntityComponent>>> entityMap;
	
	/**
	 * 
	 */
	public EntityManager() {
		entityIndices = new ArrayList<>();
		componentMap = new HashMap<>();
		scalingComponentMap = new HashMap<>();
		scaledComponentMap = new HashMap<>();
		entityMap = new HashMap<>();
	}
	
	/**
	 * XXX Slow at scale, unreasonable for arrays larger than 1024, maybe even 512
	 * Create a new entity with given components.
	 * @param components
	 */
	public void createEntity(EntityComponent...components) {
		Map<Class<? extends EntityComponent>, Integer> indices = new HashMap<>();
		for (EntityComponent component : components) {
			var componentArray = componentMap.computeIfAbsent(component.getClass(), value -> 
				(EntityComponent[]) Array.newInstance(value, 102400));
			for (int i = 0; i < componentArray.length; i++) {
				if (componentArray[i] == null) {
					componentArray[i] = component;
					indices.put(component.getClass(), i);
					break;
				}
			}
		}
		var id = UUID.randomUUID();
		entityIndices.add(new EntityIndex(id, indices));
	}
	
	/**
	 * XXX Faster than one big array, and scalable, but computationally intensive for finding the correct array to index into
	 * @param components
	 */
	public void createEntityScaling(EntityComponent...components) {
		Map<Class<? extends EntityComponent>, Integer> indices = new HashMap<>();
		for (EntityComponent component : components) {
			scalingComponentMap.compute(component.getClass(), (key, value) -> {
				if (value == null) {
					List<EntityComponent[]> componentList = new ArrayList<>();
					var componentArray = (EntityComponent[]) Array.newInstance(key, 1024);
					componentArray[0] = component;
					componentList.add(componentArray);
					indices.put(component.getClass(), 0);
					return componentList;
				} else {
					var array = value.get(value.size() - 1);
					boolean inserted = false;
					for (int i = 0; i < array.length; i++) {
						if (array[i] == null) {
							array[i] = component;
							indices.put(component.getClass(), ((value.size() - 1) * 1024) + i);
							inserted = true;
							break;
						}
					}
					if (!inserted) {
						var componentArray = (EntityComponent[]) Array.newInstance(key, 1024);
						componentArray[0] = component;
						value.add(componentArray);
						indices.put(component.getClass(), ((value.size() - 1) * 1024));
					}
					return value;
				}
			});
		}
		var id = UUID.randomUUID();
		entityIndices.add(new EntityIndex(id, indices));
	}
	
	/**
	 * XXX Faster than the multi-array approach, could likely use fine tuning to behave well
	 * @param components
	 */
	public void createEntityScaled(EntityComponent...components) {
		Map<Class<? extends EntityComponent>, Integer> indices = new HashMap<>();
		for (EntityComponent component : components) {
			scaledComponentMap.compute(component.getClass(), (key, value) -> {
				if (value == null) {
					List<EntityComponent> componentList = new ArrayList<>();
					componentList.add(component);
					indices.put(component.getClass(), 0);
					return componentList;
				} else {
					value.add(component);
					indices.put(component.getClass(), value.size() - 1);
					return value;
				}
			});
		}
		var id = UUID.randomUUID();
		entityIndices.add(new EntityIndex(id, indices));
	}
	
	/**
	 * @param components
	 */
	public void createFullEntity(EntityComponent...components) {
		Map<Class<? extends EntityComponent>, EntityComponent> componentClassMap = new HashMap<>();
		for (var component : components) {
			componentClassMap.put(component.getClass(), component);
		}
		entityMap.computeIfAbsent(componentClassMap.keySet(), key -> new HashMap<>()).put(UUID.randomUUID(), componentClassMap);
	}
	
	/**
	 * @param classes
	 * @return A Stream of EntityIndexes that have archetypes containing all supplied classes.
	 */
	@SafeVarargs
	public final Stream<EntityIndex> query(Class<? extends EntityComponent>...classes) {
		Set<Class<? extends EntityComponent>> queryArchetype = Set.of(classes);
		return entityIndices.stream()
				.filter(index -> index.archetype().keySet().containsAll(queryArchetype));
	}
	
	/**
	 * @param classes
	 * @return
	 */
	@SafeVarargs
	public final Stream<Entry<UUID, Map<Class<? extends EntityComponent>, EntityComponent>>> queryEntity(Class<? extends EntityComponent>...classes) {
		Set<Class<? extends EntityComponent>> queryArchetype = Set.of(classes);
		return entityMap.entrySet().stream()
				.filter(entry -> entry.getKey().containsAll(queryArchetype))
				.flatMap(entry -> entry.getValue().entrySet().stream());
	}
	
	/**
	 * @return
	 */
	public Map<Class<? extends EntityComponent>, EntityComponent[]> getComponentMap() {
		return componentMap;
	}
	
	public Map<Class<? extends EntityComponent>, List<EntityComponent[]>> getScalingComponentMap() {
		return scalingComponentMap;
	}
	
	public Map<Class<? extends EntityComponent>, List<EntityComponent>> getScaledComponentMap() {
		return scaledComponentMap;
	}
	
	public List<EntityIndex> getEntityIndices() {
		return entityIndices;
	}
	
	public Map<Set<Class<? extends EntityComponent>>, Map<UUID, Map<Class<? extends EntityComponent>, EntityComponent>>> getEntityMap() {
		return entityMap;
	}
	
	public static void main(String[] args) {
		var manager = new EntityManager();
		
		for (int i = 0; i < 256; i++) {
			manager.createEntity(new TComp(Math.random(), Math.random()), new MComp("Mesh"));
		}
		for (int i = 0; i < 256; i++) {
			manager.createEntity(new TComp(Math.random(), Math.random()), new AComp(14));
		}
		for (int i = 0; i < 256; i++) {
			manager.createEntity(new TComp(Math.random(), Math.random()), new MComp("Cube"), new AComp(14));
		}
		
		manager.query(TComp.class, MComp.class).forEach(entity -> {
			var transform = manager.getComponentMap().get(TComp.class)[entity.archetype().get(TComp.class)];
			if (!(transform instanceof TComp)) {
				System.out.println("We fucked up");
			}
			var model = manager.getComponentMap().get(MComp.class)[entity.archetype().get(MComp.class)];
			if (!(model instanceof MComp)) {
				System.out.println("We fucked up");
			}
		});
		
		manager.query(TComp.class, MComp.class).forEach(entity -> {
			var transform = manager.getComponentMap().get(TComp.class)[entity.archetype().get(TComp.class)];
			if (!(transform instanceof TComp)) {
				System.out.println("We fucked up");
			}
			var model = manager.getComponentMap().get(MComp.class)[entity.archetype().get(MComp.class)];
			if (!(model instanceof MComp)) {
				System.out.println("We fucked up");
			}
		});
	}
	
	private static record TComp(double x, double y) implements EntityComponent {}
	private static record MComp(String model) implements EntityComponent {}
	private static record AComp(int source) implements EntityComponent {}
	
}
