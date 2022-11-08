package com.avogine.ecs.system;

import org.joml.*;

import com.avogine.ecs.*;
import com.avogine.ecs.addons.MeshCache;
import com.avogine.ecs.components.*;
import com.avogine.game.Game;
import com.avogine.game.scene.ECSScene;
import com.avogine.game.util.*;
import com.avogine.render.loader.assimp.ModelCache;
import com.avogine.render.shader.BasicShader;

/**
 *
 */
public class RenderSystem extends EntitySystem implements Renderable, Cleanupable {
	
	private EntityComponentQuery renderQuery;
	private EntityComponentQuery modelRenderQuery;
	
	private BasicShader basicShader;
	
	private final Matrix4f model;
	
	/**
	 * @param game 
	 * 
	 */
	public RenderSystem(Game game) {
		super(game);
		renderQuery = new EntityComponentQuery(EntityArchetype.of(TransformComponent.class, MeshComponent.class));
		modelRenderQuery = new EntityComponentQuery(EntityArchetype.of(TransformComponent.class, ModelComponent.class));
		model = new Matrix4f();
	}
	
	@Override
	public void onRegister() {
		basicShader = new BasicShader("basicVertex.glsl", "basicFragment.glsl");
	}
	
	@Override
	public void onRender(SceneState sceneState) {
		if (sceneState.scene() instanceof ECSScene ecsScene) {
			renderScene(ecsScene);
		}
	}
	
	private void renderScene(ECSScene scene) {
		renderQuery.fetch(scene.getEntityWorld());
		modelRenderQuery.fetch(scene.getEntityWorld());

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
				basicShader.isTextured.loadBoolean(!mesh.getTextures().isEmpty());
				if (mesh.getTextures().isEmpty()) {
					basicShader.color.loadVec3(new Vector3f(1, 0, 0));
				}
				meshCache.getCache().get(i).render();
			}
		});
		
		modelRenderQuery.getResultMap().forEach(map -> {
			var transform = map.getAs(TransformComponent.class);
			var modelC = map.getAs(ModelComponent.class);
			
			model.identity().translationRotateScale(
					transform.getPosition().x, transform.getPosition().y, transform.getPosition().z,
					transform.getOrientation().x, transform.getOrientation().y, transform.getOrientation().z, transform.getOrientation().w,
					transform.getScale().x, transform.getScale().y, transform.getScale().z);
			basicShader.model.loadMatrix(model);

			basicShader.isTextured.loadBoolean(false);
			var realModel = ModelCache.getInstance().getModel(modelC.getModel(), "");
			realModel.render(mesh -> basicShader.color.loadVec3(new Vector3f(1)));
		});
		
		basicShader.unbind();
	}

	@Override
	public void onCleanup() {
		basicShader.cleanup();
	}

}
