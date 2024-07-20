package com.avogine.ecs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.*;

import com.avogine.TestGame;
import com.avogine.ecs.components.*;

/**
 *
 */
class ECSSystemTest {

	private TestGame game;
	private TestECSScene scene;
	private EntityManager manager;
	
	@BeforeEach
	void setup() {
		scene = new TestECSScene();
		
		game = new TestGame(scene);
		game.init(null);

		
		manager = scene.getEntityManager();
	}
	
	@AfterEach
	void teardown() {
		game.cleanup();
	}
	
	@Test
	void componentQueryTest() {
		var entityA = manager.createEntityWith(new TransformComponent(), new MeshComponent());
		var entityB = manager.createEntityWith(new TransformComponent());
		
		manager.query(TestMeshRenderable.class).forEach(meshRenderable -> {
			meshRenderable.transform().position().x += 2;
		});
		
		assertEquals(2, manager.getEntity(entityA).orElseThrow().getAs(TransformComponent.class).position().x);
		assertEquals(0, manager.getEntity(entityB).orElseThrow().getAs(TransformComponent.class).position().x);
	}
	

	@Test
	void testInlineArchetypeCasting() {
		manager.createEntityWith(new TransformComponent(), new ModelComponent(""));
		manager.createEntityWith(new ModelComponent(""), new TransformComponent());
		manager.createEntityWith(new TransformComponent(), new ModelComponent(""));
		
		manager.createEntityWith(new TransformComponent());
		manager.createEntityWith(new TransformComponent());
		
		assertEquals(2, manager.getChunks().size(), "Chunk size does not match: " + manager.getChunks().size());
		
		AtomicInteger countRender = new AtomicInteger();
		manager.query(TestRenderable.class).forEach(renderable -> {
			countRender.incrementAndGet();
		});
		assertEquals(3, countRender.get(), "Failed to find all renderables: " + countRender.get());
		
		AtomicInteger countTransform = new AtomicInteger();
		manager.query(TestTransformable.class).forEach(transformable -> {
			countTransform.incrementAndGet();
		});
		assertEquals(5, countTransform.get(), "Failed to find all transformables: " + countTransform.get());
	}

	@Test
	void testRecordArchetypeQueryModifying() {
		manager.createEntityWith(new TransformComponent(), new ModelComponent(""));
		manager.createEntityWith(new ModelComponent(""), new TransformComponent());
		manager.createEntityWith(new TransformComponent(), new ModelComponent(""));
		
		manager.createEntityWith(new TransformComponent());
		manager.createEntityWith(new TransformComponent());
		
		Map<UUID, Float> pairs = new HashMap<>();
		
		AtomicInteger movement = new AtomicInteger();
		manager.query(TestRenderable.class).forEach(renderable -> {
			pairs.put(renderable.id(), (float) movement.incrementAndGet());
			renderable.transform().setPosition(movement.get(), 0, 0);
		});
		
		manager.query(TestRenderable.class).forEach(renderable -> {
			assertEquals(pairs.get(renderable.id()), renderable.transform().position().x);
		});
	}
	
	@Test
	void testWeirdIDParamPlacement() {
		var id = manager.createEntityWith(new TransformComponent(), new ModelComponent(""));
		
		manager.query(TestWonkyArchetype.class).forEach(wonky -> {
			assertEquals(id, wonky.id());
		});
	}
	
}
