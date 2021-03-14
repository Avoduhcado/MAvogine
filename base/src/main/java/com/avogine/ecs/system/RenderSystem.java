package com.avogine.ecs.system;

import java.util.Set;

import com.avogine.ecs.EntityArchetype;
import com.avogine.ecs.EntityComponentMap;
import com.avogine.ecs.EntityComponentQuery;
import com.avogine.ecs.EntitySystem;
import com.avogine.ecs.EntityWorld;
import com.avogine.ecs.components.MeshComponent;
import com.avogine.ecs.components.TransformComponent;

/**
 *
 */
public class RenderSystem extends EntitySystem {

	private EntityComponentQuery renderQuery;
	
	/**
	 * @param entityWorld
	 */
	public RenderSystem(EntityWorld entityWorld) {
		super(entityWorld);
		renderQuery = new EntityComponentQuery(EntityArchetype.of(TransformComponent.class, MeshComponent.class));
	}
	
	@Override
	public void process() {
		Set<EntityComponentMap> componentSet = renderQuery.fetch(entityWorld.getChunks());
		for (EntityComponentMap componentMap : componentSet) {
			TransformComponent transform = componentMap.getAs(TransformComponent.class);
			MeshComponent render = componentMap.getAs(MeshComponent.class);
			
			transform.getPosition().add(1, 1, 1);
			
			System.out.println(transform.getPosition());
			System.out.println(render.toString());
		}
	}

}
