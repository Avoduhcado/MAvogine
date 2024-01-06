package com.avogine.ecs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.avogine.ecs.components.*;

/**
 *
 */
class ECSArchetypeTest {
	
	@Test
	void testArchetypeHashing() {
		EntityComponentSet arch1 = EntityComponentSet.of(TransformComponent.class);
		EntityComponentSet arch2 = EntityComponentSet.of(TransformComponent.class);

		System.out.println(System.identityHashCode(arch1) + " " + System.identityHashCode(arch2));
		assertEquals(true, arch1.equals(arch2));
		
		EntityComponentSet arch3 = EntityComponentSet.of(TransformComponent.class, ModelComponent.class);
		EntityComponentSet arch4 = EntityComponentSet.of(ModelComponent.class, TransformComponent.class);
		
		System.out.println(System.identityHashCode(arch3) + " " + System.identityHashCode(arch4));
		assertEquals(true, arch3.equals(arch4));
	}
	
}
