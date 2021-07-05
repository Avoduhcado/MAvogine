package com.avogine.ecs.system;

import java.util.Set;

import com.avogine.ecs.EntityArchetype;
import com.avogine.ecs.EntityComponentMap;
import com.avogine.ecs.EntityComponentQuery;
import com.avogine.ecs.EntitySystem;
import com.avogine.ecs.EntityWorld;
import com.avogine.ecs.components.ModelComponent;
import com.avogine.ecs.components.TransformComponent;
import com.avogine.game.scene.*;
import com.avogine.io.*;

/**
 *
 */
public class RenderSystem extends EntitySystem {

	private EntityComponentQuery renderQuery;
	
	/**
	 * 
	 */
	public RenderSystem() {
		renderQuery = new EntityComponentQuery(EntityArchetype.of(TransformComponent.class, ModelComponent.class));
	}
	
	@Override
	public void init(Window window) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 
	 */
	public void process(ECSScene scene) {
		Set<EntityComponentMap> componentSet = renderQuery.fetch(scene.getEntityWorld().getChunks());
		for (EntityComponentMap componentMap : componentSet) {
			TransformComponent transform = componentMap.getAs(TransformComponent.class);
			ModelComponent render = componentMap.getAs(ModelComponent.class);
			
			transform.getPosition().add(1, 1, 1);
			
			System.out.println(transform.getPosition());
			System.out.println(render.toString());
		}
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

}
