package com.avogine.ecs;

import static org.junit.Assert.*;

import java.io.*;

import org.joml.*;
import org.junit.jupiter.api.*;

import com.avogine.ecs.components.*;
import com.avogine.io.serializer.*;
import com.fasterxml.jackson.core.*;

/**
 *
 */
class ECSWorldTest {
	
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
	void spawnUniqueEntities() {
		// TODO Generate some entity IDs to store in the world and attach components to
		// Verify that each ID generated is unique; store them in a Set and assert the length?
		long entity1 = world.createEntity();
		long entity2 = world.createEntity();
		long entity3 = world.createEntity();
		
		assertNotEquals(entity1, entity2);
		assertNotEquals(entity2, entity3);
		
		EntityArchetype arch1 = new EntityArchetype(TransformComponent.class, MeshComponent.class);
		EntityArchetype arch2 = new EntityArchetype(TransformComponent.class, MeshComponent.class);
		EntityArchetype arch3 = new EntityArchetype(TransformComponent.class);
		
		assertEquals(arch1, arch2);
		assertNotEquals(arch2, arch3);
		
		world.createEntityWith(arch1);
		world.createEntityWith(arch2);
		
		assertEquals(2, world.getChunks().size());
		
		world.createEntityWith(new TransformComponent(new Vector3f(1, 5, 3)), new MeshComponent());
		world.createEntityWith(new TransformComponent(new Vector3f(7, 12, 31)), new MeshComponent());

		assertEquals(2, world.getChunks().size());
		
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
		world.createEntity();
		assertEquals(1, world.getChunks().size());
	}
	
	@Test
	void appendToEmptyEntity() {
		long entity1 = world.createEntity();
		assertEquals(1, world.getChunks().size());
		
		world.addComponent(entity1, new TransformComponent());
		assertEquals(1, world.getChunks().size());
		assertEquals(1, world.getEntity(entity1).size());
	}
	
	@Test
	void appendToAddNewEntity() {
		long unsafeEntityID = 1;
		world.addComponent(unsafeEntityID, new TransformComponent());
		assertEquals(1, world.getChunks().size());
		assertEquals(1, world.getEntity(unsafeEntityID).size());
	}
	
	@Test
	void removeEntity() {
		long entity = world.createEntity();
		assertEquals(1, world.getChunks().size());
		
		world.removeEntity(entity);
		assertEquals(0, world.getChunks().size());
	}
	
	@Test
	void removeComponent() {
		long entity1 = world.createEntity();
		assertEquals(1, world.getChunks().size());
		
		var entityTransform = new TransformComponent();
		world.addComponent(entity1, entityTransform);
		assertEquals(1, world.getChunks().size());
		assertEquals(1, world.getEntity(entity1).size());
		
		world.removeComponent(entity1, entityTransform);
		assertEquals(1, world.getChunks().size());
		assertEquals(0, world.getEntity(entity1).size());
	}
	
}
