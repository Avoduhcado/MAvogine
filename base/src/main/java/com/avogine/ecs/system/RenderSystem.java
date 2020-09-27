package com.avogine.ecs.system;

import java.util.Set;

import com.avogine.ecs.EntityArchetype;
import com.avogine.ecs.EntityComponentMap;
import com.avogine.ecs.EntityComponentQuery;
import com.avogine.ecs.EntitySystem;
import com.avogine.ecs.EntityWorld;
import com.avogine.ecs.components.MeshRender;
import com.avogine.ecs.components.Transform;

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
		renderQuery = new EntityComponentQuery(new EntityArchetype(Transform.class, MeshRender.class));
	}
	
	@Override
	public void process() {
		Set<EntityComponentMap> componentSet = renderQuery.fetch(entityWorld.getChunks());
		for (EntityComponentMap componentMap : componentSet) {
			Transform transform = componentMap.getAs(Transform.class);
			MeshRender render = componentMap.getAs(MeshRender.class);
			
			transform.getPosition().add(1, 1, 1);
			
			System.out.println(transform.getPosition());
			System.out.println(render.toString());
		}
	}

}
