package com.avogine.ecs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.*;

import com.avogine.ecs.components.*;

/**
 *
 */
class ECSSystemTest {

	private TestGame game;
	private TestScene scene;
	private EntityManager manager;
	
	@BeforeEach
	void setup() {
		game = new TestGame();
		game.init(null, null, null);
		
		scene = new TestScene();
		scene.init(game, null);
		
		manager = scene.getEntityManager();
	}
	
	@AfterEach
	void teardown() {
		game.cleanup();
	}
	
	@Test
	void componentQueryTest() {
		var aTransform = new TransformComponent();
		var bTransform = new TransformComponent();
		manager.createEntityWith(aTransform, new ModelComponent(""));
		manager.createEntityWith(bTransform);
		
		manager.query(TransformComponent.class, ModelComponent.class).forEach(chunk -> {
			for (int i = 0; i < chunk.getChunkSize(); i++) {
				var transform = chunk.getAs(TransformComponent.class, i);
				transform.x(transform.x() + 2);
			}
		});
		
		assertEquals(2, aTransform.x());
		assertEquals(0, bTransform.x());
	}
	

	@Test
	void testInlineArchetypeCasting() {
		manager.createEntityWith(new TransformComponent(), new ModelComponent(""));
		manager.createEntityWith(new ModelComponent(""), new TransformComponent());
		manager.createEntityWith(new TransformComponent(), new ModelComponent(""));
		
		manager.createEntityWith(new TransformComponent());
		manager.createEntityWith(new TransformComponent());
		
		assertEquals(2, manager.query().count(), "Chunk size does not match: " + manager.query().count());
		
		int transformModelCount = manager.query(TransformComponent.class, ModelComponent.class).mapToInt(EntityChunk::getChunkSize).sum();
		assertEquals(3, transformModelCount, "Failed to find all renderables: " + transformModelCount);
		
		int transformCount = manager.query(TransformComponent.class).mapToInt(EntityChunk::getChunkSize).sum();
		assertEquals(5, transformCount, "Failed to find all transformables: " + transformCount);
	}

}
