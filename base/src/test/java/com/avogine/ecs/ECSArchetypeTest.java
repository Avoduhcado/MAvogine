package com.avogine.ecs;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import com.avogine.ecs.components.*;

/**
 *
 */
class ECSArchetypeTest {

	@Test
	void testArchetypeHashing() {
		EntityArchetype arch1 = EntityArchetype.of(TransformComponent.class);
		EntityArchetype arch2 = EntityArchetype.of(TransformComponent.class);

		System.out.println(System.identityHashCode(arch1) + " " + System.identityHashCode(arch2));
		assertEquals(true, arch1.equals(arch2));
		
		EntityArchetype arch3 = EntityArchetype.of(TransformComponent.class, ModelComponent.class);
		EntityArchetype arch4 = EntityArchetype.of(ModelComponent.class, TransformComponent.class);
		
		System.out.println(System.identityHashCode(arch3) + " " + System.identityHashCode(arch4));
		assertEquals(true, arch3.equals(arch4));
	}

}
