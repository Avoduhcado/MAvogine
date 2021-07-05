package com.avogine.ecs;

import static org.junit.Assert.*;

import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.avogine.ecs.components.ModelComponent;
import com.avogine.ecs.components.TransformComponent;
import com.avogine.ecs.system.RenderSystem;

/**
 *
 */
class ECSTest {
	
	private TestScene scene;
	private EntityWorld world;
	
	@BeforeEach
	void setup() {
		scene = new TestScene();
		world = scene.getEntityWorld();
	}
	
	@Test
	void spawnUniqueEntities() {
		
		// TODO Generate some entity IDs to store in the world and attach components to
		// Verify that each ID generated is unique; store them in a Set and assert the length?
		long entity1 = world.createEntity();
		long entity2 = world.createEntity();
		long entity3 = world.createEntity();
		
		assertTrue(entity1 != entity2 && entity2 != entity3);
		
		EntityArchetype arch1 = new EntityArchetype(TransformComponent.class, ModelComponent.class);
		EntityArchetype arch2 = new EntityArchetype(TransformComponent.class, ModelComponent.class);
		EntityArchetype arch3 = new EntityArchetype(TransformComponent.class);
		
		assertTrue(arch1.equals(arch2));
		assertTrue(!arch1.equals(arch3));
		
		long entity4 = world.createEntityWith(arch1);
		long entity5 = world.createEntityWith(arch2);
		
		assertEquals(1, world.getChunks().size());
		
		long entity6 = world.createEntityWith(new TransformComponent(new Vector3f(1, 5, 3)), new ModelComponent());
		long entity7 = world.createEntityWith(new TransformComponent(new Vector3f(7, 12, 31)), new ModelComponent());
		
		RenderSystem renderSystem = new RenderSystem();
		renderSystem.process(scene);
		renderSystem.process(scene);
	}
	
}
