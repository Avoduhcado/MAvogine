package com.avogine.experimental.benchmark;

import java.text.NumberFormat;
import java.util.*;

import com.avogine.ecs.experimental.*;

/**
 *
 */
public class ECSArraySpeed {

	private static record TComp(double x, double y) implements EntityComponent {}
	private static record MComp(String model) implements EntityComponent {}
	private static record AComp(int source) implements EntityComponent {}
	private static record PComp(float mass) implements EntityComponent {}
	
	private static record Entity(TComp t, MComp m, AComp a, PComp p) {}
	
	private static int aSize = 2560;
	private static int bSize = 1280;
	private static int cSize = 2560;
	
	private final EntityManager manager;
	private final List<Entity> entityList;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		var speedTest = new ECSArraySpeed();
		
		speedTest.benchmarkFixedArrayQuery(500);
		speedTest.benchmarkScalingArrayQuery(500);
		speedTest.benchmarkArrayListQuery(500);
		speedTest.benchmarkMapQuery(500);
		speedTest.benchmarkListQuery(500);
	}
	
	/**
	 * 
	 */
	public ECSArraySpeed() {
		manager = new EntityManager();
		entityList = new ArrayList<>();
	}
	
	private void benchmarkFixedArrayQuery(int loopCount) {
		for (int i = 0; i < aSize; i++) {
			manager.createEntity(new TComp(Math.random(), Math.random()), new MComp("Mesh"));
		}
		for (int i = 0; i < bSize; i++) {
			manager.createEntity(new TComp(Math.random(), Math.random()), new AComp(14));
		}
		for (int i = 0; i < cSize; i++) {
			manager.createEntity(new TComp(Math.random(), Math.random()), new MComp("Cube"), new AComp(14));
		}
		List<Long> benchTimes = new ArrayList<>();
		
		for (int i = 0; i < loopCount; i++) {
			long startTime = System.nanoTime();
			manager.query(TComp.class, MComp.class).forEach(entity -> {
				var transform = manager.getComponentMap().get(TComp.class)[entity.archetype().get(TComp.class)];
				var model = manager.getComponentMap().get(MComp.class)[entity.archetype().get(MComp.class)];
			});
			benchTimes.add(System.nanoTime() - startTime);
		}
		
		System.out.println("Fixed array time avg: " + NumberFormat.getNumberInstance().format(benchTimes.stream().skip(10).mapToLong(Long::valueOf).average().orElseThrow()) + "ns");
		
		manager.getComponentMap().clear();
		manager.getEntityIndices().clear();
	}

	private void benchmarkScalingArrayQuery(int loopCount) {
		for (int i = 0; i < aSize; i++) {
			manager.createEntityScaling(new TComp(Math.random(), Math.random()), new MComp("Mesh"));
		}
		for (int i = 0; i < bSize; i++) {
			manager.createEntityScaling(new TComp(Math.random(), Math.random()), new AComp(14));
		}
		for (int i = 0; i < cSize; i++) {
			manager.createEntityScaling(new TComp(Math.random(), Math.random()), new MComp("Cube"), new AComp(14));
		}
		List<Long> benchTimes = new ArrayList<>();

		for (int i = 0; i < loopCount; i++) {
			long startTime = System.nanoTime();
			manager.query(TComp.class, MComp.class).forEach(entity -> {
				var transform = manager.getScalingComponentMap().get(TComp.class).get(entity.archetype().get(TComp.class) / 1024)[entity.archetype().get(TComp.class) % 1024];
				var model = manager.getScalingComponentMap().get(MComp.class).get(entity.archetype().get(MComp.class) / 1024)[entity.archetype().get(MComp.class) % 1024];
			});
			benchTimes.add(System.nanoTime() - startTime);
		}

		System.out.println("Scaling array time avg: " + NumberFormat.getNumberInstance().format(benchTimes.stream().skip(10).mapToLong(Long::valueOf).average().orElseThrow()) + "ns");
		
		manager.getScalingComponentMap().clear();
		manager.getEntityIndices().clear();
	}

	private void benchmarkArrayListQuery(int loopCount) {
		for (int i = 0; i < aSize; i++) {
			manager.createEntityScaled(new TComp(Math.random(), Math.random()), new MComp("Mesh"));
		}
		for (int i = 0; i < bSize; i++) {
			manager.createEntityScaled(new TComp(Math.random(), Math.random()), new AComp(14));
		}
		for (int i = 0; i < cSize; i++) {
			manager.createEntityScaled(new TComp(Math.random(), Math.random()), new MComp("Cube"), new AComp(14));
		}
		List<Long> benchTimes = new ArrayList<>();

		for (int i = 0; i < loopCount; i++) {
			long startTime = System.nanoTime();

			manager.query(TComp.class, MComp.class).forEach(entity -> {
				var transform = manager.getScaledComponentMap().get(TComp.class).get(entity.archetype().get(TComp.class));
				var model = manager.getScaledComponentMap().get(MComp.class).get(entity.archetype().get(MComp.class));
			});
			benchTimes.add(System.nanoTime() - startTime);
		}

		System.out.println("ArrayList time avg: " + NumberFormat.getNumberInstance().format(benchTimes.stream().skip(10).mapToLong(Long::valueOf).average().orElseThrow()) + "ns");

		manager.getScaledComponentMap().clear();
		manager.getEntityIndices().clear();
	}
	
	private void benchmarkMapQuery(int loopCount) {
		for (int i = 0; i < aSize; i++) {
			manager.createFullEntity(new TComp(Math.random(), Math.random()), new MComp("Mesh"));
		}
		for (int i = 0; i < bSize; i++) {
			manager.createFullEntity(new TComp(Math.random(), Math.random()), new AComp(14));
		}
		for (int i = 0; i < cSize; i++) {
			manager.createFullEntity(new TComp(Math.random(), Math.random()), new MComp("Cube"), new AComp(14));
		}
		List<Long> benchTimes = new ArrayList<>();

		for (int i = 0; i < loopCount; i++) {
			long startTime = System.nanoTime();
			manager.queryEntity(TComp.class, MComp.class).forEach(entity -> {
				var transform = entity.getValue().get(TComp.class);
				var model = entity.getValue().get(MComp.class);
			});
			benchTimes.add(System.nanoTime() - startTime);
		}

		System.out.println("Entity map time avg: " + NumberFormat.getNumberInstance().format(benchTimes.stream().skip(10).mapToLong(Long::valueOf).average().orElseThrow()) + "ns");
		
		manager.getEntityMap().clear();
	}
	
	private void benchmarkListQuery(int loopCount) {
		for (int i = 0; i < aSize; i++) {
			entityList.add(new Entity(new TComp(Math.random(), Math.random()), new MComp("Mesh"), null, null));
		}
		for (int i = 0; i < bSize; i++) {
			entityList.add(new Entity(new TComp(Math.random(), Math.random()), null, new AComp(14), null));
		}
		for (int i = 0; i < cSize; i++) {
			entityList.add(new Entity(new TComp(Math.random(), Math.random()), new MComp("Cube"), new AComp(14), null));
		}
		List<Long> benchTimes = new ArrayList<>();

		for (int i = 0; i < loopCount; i++) {
			long startTime = System.nanoTime();
			entityList.stream()
			.filter(entity -> entity.t() != null && entity.m() != null)
			.forEach(renderable -> {
				var transform = renderable.t();
				var model = renderable.m();
			});
			benchTimes.add(System.nanoTime() - startTime);
		}

		System.out.println("Entity list time avg: " + NumberFormat.getNumberInstance().format(benchTimes.stream().skip(10).mapToLong(Long::valueOf).average().orElseThrow()) + "ns");
		
		entityList.clear();
	}
}
