package com.avogine.ecs.system;

import com.avogine.ecs.EntityArchetype;
import com.avogine.ecs.EntityComponentQuery;
import com.avogine.ecs.EntitySystem;
import com.avogine.ecs.components.MeshComponent;
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
		renderQuery = new EntityComponentQuery(EntityArchetype.of(TransformComponent.class, MeshComponent.class));
	}
	
	@Override
	public void init(Window window) {
		// Not implemented
	}
	
	/**
	 * @param scene 
	 * 
	 */
	public void process(ECSScene scene) {
		renderQuery.fetch(scene.getEntityWorld());
		
		renderQuery.getResultMap().forEach(componentMap -> {
			TransformComponent transform = componentMap.getAs(TransformComponent.class);
			MeshComponent render = componentMap.getAs(MeshComponent.class);
			
			transform.getPosition().add(1, 1, 1);
			
			System.out.println(transform.getPosition());
			System.out.println(render.toString());
		});
	}

	@Override
	public void cleanup() {
		// Not implemented
	}

}
