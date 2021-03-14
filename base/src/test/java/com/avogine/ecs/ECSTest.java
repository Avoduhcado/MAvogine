package com.avogine.ecs;

import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.avogine.ecs.components.MeshComponent;
import com.avogine.ecs.components.TransformComponent;
import com.avogine.ecs.system.RenderSystem;

/**
 *
 */
class ECSTest {
	
	private EntityWorld world;
	
	@BeforeEach
	void setup() {
		// TODO Setup EntityWorld
		world = new EntityWorld();
	}
	
	@Test
	void spawnUniqueEntities() {
		// TODO Generate some entity IDs to store in the world and attach components to
		// Verify that each ID generated is unique; store them in a Set and assert the length?
		long entity1 = world.createEntity();
		long entity2 = world.createEntity();
		long entity3 = world.createEntity();
		
		assert(entity1 != entity2 && entity2 != entity3);
		
		EntityArchetype arch1 = new EntityArchetype(TransformComponent.class, MeshComponent.class);
		EntityArchetype arch2 = new EntityArchetype(TransformComponent.class, MeshComponent.class);
		EntityArchetype arch3 = new EntityArchetype(TransformComponent.class);
		
		assert(arch1.equals(arch2));
		assert(!arch1.equals(arch3));
		
		long entity4 = world.createEntityWith(arch1);
		long entity5 = world.createEntityWith(arch2);
		
		assert(world.getChunks().size() == 1);
		
		long entity6 = world.createEntityWith(new TransformComponent(new Vector3f(1, 5, 3)), new MeshComponent());
		long entity7 = world.createEntityWith(new TransformComponent(new Vector3f(7, 12, 31)), new MeshComponent());
		
		RenderSystem renderSystem = new RenderSystem(world);
		renderSystem.process();
		renderSystem.process();
	}
	
}
