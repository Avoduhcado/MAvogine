package com.avogine.ecs;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.*;

import com.avogine.ecs.components.*;
import com.avogine.io.serializer.SceneMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 *
 */
class ECSWorldTest {
	
	private TestGame game;
	private TestScene scene;
	private EntityManager world;
	
	@BeforeEach
	void setup() {
		game = new TestGame();
		game.init(null, null, null);
		
		scene = new TestScene();
		scene.init(game, null);
		
		world = scene.getEntityManager();
	}
	
	@AfterEach
	void teardown() {
		game.cleanup();
	}
	
	@Test
	void spawnUniqueEntities() {
		// TODO Generate some entity IDs to store in the world and attach components to
		// Verify that each ID generated is unique; store them in a Set and assert the length?
		var entity1 = world.createEntityWith();
		var entity2 = world.createEntityWith();
		var entity3 = world.createEntityWith();
		
		assertNotEquals(entity1, entity2);
		assertNotEquals(entity2, entity3);
		
		world.createEntityWith(new TransformComponent(), new ModelComponent(""));
		world.createEntityWith(new TransformComponent(), new ModelComponent(""));
		
		assertEquals(2, world.query().count());
		
		world.createEntityWith(new TransformComponent(1, 5, 3), new ModelComponent(""));
		world.createEntityWith(new TransformComponent(7, 12, 31), new ModelComponent(""));

		assertEquals(2, world.query().count());
		
		try {
			SceneMapper.serializeScene(scene);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	void addEmptyEntity() {
		world.createEntityWith();
		assertEquals(1, world.query().count());
	}
	
	// TODO Re-implement adding components to existing entities
//	@Test
//	void appendToEmptyEntity() {
//		var entity1 = world.createEntityWith();
//		assertEquals(1, world.query().count());
//		
//		world.addComponent(entity1, new TransformComponent());
//		assertEquals(1, world.getChunks().size());
//		assertEquals(1, world.getEntity(entity1).orElseThrow().size());
//	}
//	
//	@Test
//	void appendToAddNewEntity() {
//		var unsafeEntityID = UUID.randomUUID();
//		world.addComponent(unsafeEntityID, new TransformComponent());
//		assertEquals(1, world.getChunks().size());
//		assertEquals(1, world.getEntity(unsafeEntityID).orElseThrow().size());
//	}
	
	@Test
	void removeEntity() {
		var entity = world.createEntityWith();
		assertEquals(1, world.query().count());
		
		world.removeEntity(entity);
		assertEquals(0, world.query().count());
	}
	
//	@Test
//	void removeComponent() {
//		var entity1 = world.createEntity();
//		assertEquals(1, world.getChunks().size());
//		
//		var entityTransform = new TransformComponent();
//		world.addComponent(entity1, entityTransform);
//		assertEquals(1, world.getChunks().size());
//		assertEquals(1, world.getEntity(entity1).orElseThrow().size());
//		
//		world.removeComponent(entity1, entityTransform);
//		assertEquals(1, world.getChunks().size());
//		assertEquals(0, world.getEntity(entity1).orElseThrow().size());
//	}
	
}
