package com.avogine.ecs;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import com.avogine.ecs.components.*;

/**
 *
 */
class ECSSystemTest {

	private TestGame game;
	private TestScene scene;
	private EntityWorld world;
	
	@BeforeEach
	void setup() {
		game = new TestGame();
		game.init(null);
		
		scene = new TestScene();
		scene.init(game, null);
		
		world = scene.getEntityWorld();
	}
	
	@AfterEach
	void teardown() {
		game.cleanup();
	}
	
	@Test
	void componentQueryTest() {
		long entityA = world.createEntityWith(EntityArchetype.of(TransformComponent.class, MeshComponent.class));
		long entityB = world.createEntityWith(EntityArchetype.of(TransformComponent.class));
		
		var query = new EntityComponentQuery(EntityArchetype.of(TransformComponent.class, MeshComponent.class));
		query.fetch(world);
		
		query.getResultMap().forEach(map -> {
			var transform = map.getAs(TransformComponent.class);
			transform.getPosition().x += 2;
		});
		
		assertEquals(2, world.getEntity(entityA).getAs(TransformComponent.class).getPosition().x);
		assertEquals(0, world.getEntity(entityB).getAs(TransformComponent.class).getPosition().x);
	}

}
