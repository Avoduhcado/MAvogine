package com.avogine.ecs.system;

import org.joml.*;

import com.avogine.ecs.*;
import com.avogine.ecs.addons.*;
import com.avogine.ecs.components.*;
import com.avogine.game.*;
import com.avogine.game.scene.*;
import com.avogine.game.util.*;
import com.avogine.io.*;
import com.avogine.render.shader.*;

/**
 *
 */
public class RenderSystem extends EntitySystem implements Renderable, Cleanupable {
	
	private EntityComponentQuery renderQuery;
	
	private BasicShader basicShader;
	
	private final Matrix4f model;
	
	/**
	 * 
	 */
	public RenderSystem() {
		renderQuery = new EntityComponentQuery(EntityArchetype.of(TransformComponent.class, MeshComponent.class));
		model = new Matrix4f();
	}
	
	@Override
	public void init(Game game, Window window) {
		if (initialized) {
			return;
		}
		basicShader = new BasicShader("basicVertex.glsl", "basicFragment.glsl");
		
		register(game);
		initialized = true;
	}
	
	@Override
	public void onRender(SceneState sceneState) {
		if (sceneState.scene() instanceof ECSScene ecsScene) {
			renderScene(ecsScene);
		}
	}
	
	private void renderScene(ECSScene scene) {
		renderQuery.fetch(scene.getEntityWorld());

		basicShader.bind();
		
		basicShader.projection.loadMatrix(scene.getProjection());
		basicShader.view.loadMatrix(scene.getView());
		var meshCache = scene.getEntityWorld().getAddon(MeshCache.class);
		
		renderQuery.getResultMap().forEach(componentMap -> {
			TransformComponent transform = componentMap.getAs(TransformComponent.class);
			MeshComponent render = componentMap.getAs(MeshComponent.class);
			
			model.identity().translationRotateScale(
					transform.getPosition().x, transform.getPosition().y, transform.getPosition().z,
					transform.getOrientation().x, transform.getOrientation().y, transform.getOrientation().z, transform.getOrientation().w,
					transform.getScale().x, transform.getScale().y, transform.getScale().z);
			basicShader.model.loadMatrix(model);
			
			for (int i : render.getMeshes()) {
				var mesh = meshCache.getCache().get(i);
				basicShader.isTextured.loadBoolean(mesh.getMaterial().isTextured());
				if (!mesh.getMaterial().isTextured()) {
					basicShader.color.loadVec3(mesh.getMaterial().getDiffuseColor());
				}
				meshCache.getCache().get(i).render();
			}
		});
		
		basicShader.unbind();
	}

	@Override
	public void onCleanup() {
		basicShader.cleanup();
	}
	
	@Override
	public void register(Game game) {
		Renderable.super.register(game);
		Cleanupable.super.register(game);
	}

}
