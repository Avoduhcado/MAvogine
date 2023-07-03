package com.avogine.ecs.experimental;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

/**
 *
 */
class ArrayECSTest {

	private static record TComp(double x, double y) implements EntityComponent {}
	private static record MComp(String model) implements EntityComponent {}
	private static record AComp(int source) implements EntityComponent {}
	private static record PComp(float mass) implements EntityComponent {}
	
	private static EntityManager manager;
	
	@BeforeAll
	public static void setup() {
		manager = new EntityManager();
	}
	
	@Test
	public void testQuerying() {
		for (int i = 0; i < 25600; i++) {
			manager.createEntity(new TComp(Math.random(), Math.random()), new MComp("Mesh"));
		}
		for (int i = 0; i < 12800; i++) {
			manager.createEntity(new TComp(Math.random(), Math.random()), new AComp(14));
		}
		for (int i = 0; i < 25600; i++) {
			manager.createEntity(new TComp(Math.random(), Math.random()), new MComp("Cube"), new AComp(14));
		}
		
		long queryASize = manager.query(TComp.class, MComp.class).peek(entity -> {
			var transform = manager.getComponentMap().get(TComp.class)[entity.archetype().get(TComp.class)];
			assertInstanceOf(TComp.class, transform);
			var model = manager.getComponentMap().get(MComp.class)[entity.archetype().get(MComp.class)];
			assertInstanceOf(MComp.class, model);
		}).count();
		assertEquals(51200, queryASize);
		
		long queryBSize = manager.query(TComp.class, AComp.class).peek(entity -> {
			var transform = manager.getComponentMap().get(TComp.class)[entity.archetype().get(TComp.class)];
			assertInstanceOf(TComp.class, transform);
			var audio = manager.getComponentMap().get(AComp.class)[entity.archetype().get(AComp.class)];
			assertInstanceOf(AComp.class, audio);
		}).count();
		assertEquals(38400, queryBSize);
		
		long queryCSize = manager.query(TComp.class, MComp.class, AComp.class).peek(entity -> {
			var transform = manager.getComponentMap().get(TComp.class)[entity.archetype().get(TComp.class)];
			assertInstanceOf(TComp.class, transform);
			var audio = manager.getComponentMap().get(AComp.class)[entity.archetype().get(AComp.class)];
			assertInstanceOf(AComp.class, audio);
		}).count();
		assertEquals(25600, queryCSize);
		
		long queryDSize = manager.query(TComp.class, PComp.class).count();
		assertEquals(0, queryDSize);

		manager.getComponentMap().clear();
		manager.getEntityIndices().clear();
	}
	
	@Test
	public void testScalingQuerying() {
		for (int i = 0; i < 25600; i++) {
			manager.createEntityScaling(new TComp(Math.random(), Math.random()), new MComp("Mesh"));
		}
		for (int i = 0; i < 12800; i++) {
			manager.createEntityScaling(new TComp(Math.random(), Math.random()), new AComp(14));
		}
		for (int i = 0; i < 25600; i++) {
			manager.createEntityScaling(new TComp(Math.random(), Math.random()), new MComp("Cube"), new AComp(14));
		}
		
		long queryASize = manager.query(TComp.class, MComp.class).peek(entity -> {
			var transform = manager.getScalingComponentMap().get(TComp.class).get(entity.archetype().get(TComp.class) / 1024)[entity.archetype().get(TComp.class) % 1024];
			assertInstanceOf(TComp.class, transform);
			var model = manager.getScalingComponentMap().get(MComp.class).get(entity.archetype().get(MComp.class) / 1024)[entity.archetype().get(MComp.class) % 1024];
			assertInstanceOf(MComp.class, model);
		}).count();
		assertEquals(51200, queryASize);
		
		long queryBSize = manager.query(TComp.class, AComp.class).peek(entity -> {
			var transform = manager.getScalingComponentMap().get(TComp.class).get(entity.archetype().get(TComp.class) / 1024)[entity.archetype().get(TComp.class) % 1024];
			assertInstanceOf(TComp.class, transform);
			var audio = manager.getScalingComponentMap().get(AComp.class).get(entity.archetype().get(AComp.class) / 1024)[entity.archetype().get(AComp.class) % 1024];
			assertInstanceOf(AComp.class, audio);
		}).count();
		assertEquals(38400, queryBSize);
		
		long queryCSize = manager.query(TComp.class, MComp.class, AComp.class).peek(entity -> {
			var transform = manager.getScalingComponentMap().get(TComp.class).get(entity.archetype().get(TComp.class) / 1024)[entity.archetype().get(TComp.class) % 1024];
			assertInstanceOf(TComp.class, transform);
			var model = manager.getScalingComponentMap().get(MComp.class).get(entity.archetype().get(MComp.class) / 1024)[entity.archetype().get(MComp.class) % 1024];
			assertInstanceOf(MComp.class, model);
			var audio = manager.getScalingComponentMap().get(AComp.class).get(entity.archetype().get(AComp.class) / 1024)[entity.archetype().get(AComp.class) % 1024];
			assertInstanceOf(AComp.class, audio);
		}).count();
		assertEquals(25600, queryCSize);
		
		long queryDSize = manager.query(TComp.class, PComp.class).count();
		assertEquals(0, queryDSize);
		
		manager.getScalingComponentMap().clear();
		manager.getEntityIndices().clear();
	}

	@Test
	public void testScaledQuerying() {
		for (int i = 0; i < 25600; i++) {
			manager.createEntityScaled(new TComp(Math.random(), Math.random()), new MComp("Mesh"));
		}
		for (int i = 0; i < 12800; i++) {
			manager.createEntityScaled(new TComp(Math.random(), Math.random()), new AComp(14));
		}
		for (int i = 0; i < 25600; i++) {
			manager.createEntityScaled(new TComp(Math.random(), Math.random()), new MComp("Cube"), new AComp(14));
		}
		
		long queryASize = manager.query(TComp.class, MComp.class).peek(entity -> {
			var transform = manager.getScaledComponentMap().get(TComp.class).get(entity.archetype().get(TComp.class));
			assertInstanceOf(TComp.class, transform);
			var model = manager.getScaledComponentMap().get(MComp.class).get(entity.archetype().get(MComp.class));
			assertInstanceOf(MComp.class, model);
		}).count();
		assertEquals(51200, queryASize);
		
		long queryBSize = manager.query(TComp.class, AComp.class).peek(entity -> {
			var transform = manager.getScaledComponentMap().get(TComp.class).get(entity.archetype().get(TComp.class));
			assertInstanceOf(TComp.class, transform);
			var audio = manager.getScaledComponentMap().get(AComp.class).get(entity.archetype().get(AComp.class));
			assertInstanceOf(AComp.class, audio);
		}).count();
		assertEquals(38400, queryBSize);
		
		long queryCSize = manager.query(TComp.class, MComp.class, AComp.class).peek(entity -> {
			var transform = manager.getScaledComponentMap().get(TComp.class).get(entity.archetype().get(TComp.class));
			assertInstanceOf(TComp.class, transform);
			var model = manager.getScaledComponentMap().get(MComp.class).get(entity.archetype().get(MComp.class));
			assertInstanceOf(MComp.class, model);
			var audio = manager.getScaledComponentMap().get(AComp.class).get(entity.archetype().get(AComp.class));
			assertInstanceOf(AComp.class, audio);
		}).count();
		assertEquals(25600, queryCSize);
		
		long queryDSize = manager.query(TComp.class, PComp.class).count();
		assertEquals(0, queryDSize);
		
		manager.getScaledComponentMap().clear();
		manager.getEntityIndices().clear();
	}
}
