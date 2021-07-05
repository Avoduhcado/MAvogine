package com.avogine.ecs;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import com.avogine.ecs.components.*;
import com.avogine.ecs.system.*;

/**
 *
 */
class ECSSystemTest {

	private TestScene scene;
	private EntityWorld world;
	
	@BeforeEach
	void setup() {
		scene = new TestScene();
		scene.init(null);
		
		world = scene.getEntityWorld();
	}
	
	@AfterEach
	void teardown() {
		scene.cleanup();
	}
	
	@Test
	void renderSystemTest() {
		RenderSystem renderSystem = new RenderSystem();
		renderSystem.init(null);
		
		long entityA = world.createEntityWith(EntityArchetype.of(TransformComponent.class, ModelComponent.class));
		long entityB = world.createEntityWith(EntityArchetype.of(TransformComponent.class));
		
		renderSystem.process(scene);
		renderSystem.process(scene);
		assertEquals(2, world.getEntity(entityA).getAs(TransformComponent.class).getPosition().x);
		assertEquals(0, world.getEntity(entityB).getAs(TransformComponent.class).getPosition().x);
		
		renderSystem.cleanup();
	}

}
