package com.avogine.ecs.system;

import org.joml.*;

import com.avogine.ecs.*;
import com.avogine.ecs.components.*;
import com.avogine.game.Game;
import com.avogine.game.scene.ECSScene;
import com.avogine.game.util.*;
import com.avogine.render.data.material.PBRMaterial;
import com.avogine.render.loader.assimp.ModelCache;
import com.avogine.render.shader.BasicShader;

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
		renderQuery = new EntityComponentQuery(EntityArchetype.of(TransformComponent.class, ModelComponent.class));
		model = new Matrix4f();
	}
	
	@Override
	public void onRegister(Game game) {
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

		basicShader.bind();
		
		basicShader.projection.loadMatrix(scene.getProjection());
		basicShader.view.loadMatrix(scene.getView());
		var modelCache = scene.getEntityWorld().getAddon(ModelCache.class);
		
		renderQuery.getResultMap().forEach(map -> {
			var transform = map.getAs(TransformComponent.class);
			var modelC = map.getAs(ModelComponent.class);
			
			model.identity().translationRotateScale(
					transform.getPosition().x, transform.getPosition().y, transform.getPosition().z,
					transform.getOrientation().x, transform.getOrientation().y, transform.getOrientation().z, transform.getOrientation().w,
					transform.getScale().x, transform.getScale().y, transform.getScale().z);
			basicShader.model.loadMatrix(model);

			var realModel = modelCache.getModel(modelC.getModel(), "");
			if (realModel.getMaterial(0) instanceof PBRMaterial tex) {
				basicShader.uvTransform.loadMatrix(tex.uvTransform());
			} else {
				basicShader.uvTransform.loadMatrix(new Matrix3f());
			}
			realModel.render();
		});
		
		basicShader.unbind();
	}

	@Override
	public void onCleanup() {
		basicShader.cleanup();
	}

}
