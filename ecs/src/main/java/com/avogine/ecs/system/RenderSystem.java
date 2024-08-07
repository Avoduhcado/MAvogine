package com.avogine.ecs.system;

import java.util.UUID;

import org.joml.Matrix4f;

import com.avogine.ecs.*;
import com.avogine.ecs.addons.ModelCache;
import com.avogine.ecs.components.*;
import com.avogine.game.HotGame;
import com.avogine.game.scene.ECSScene;
import com.avogine.game.util.*;
import com.avogine.io.Window;
import com.avogine.render.shader.BasicShader;

/**
 *
 */
public class RenderSystem extends EntitySystem implements Renderable, Cleanupable {

	private record Renderable(UUID id, TransformComponent transform, ModelComponent modelComponent) implements EntityArchetype {}
	
	private BasicShader basicShader;
	
	private final Matrix4f model;
	
	/**
	 * 
	 */
	public RenderSystem() {
		model = new Matrix4f();
	}
	
	@Override
	public void onRegister(HotGame game) {
		basicShader = new BasicShader("basicVertex.glsl", "basicFragment.glsl");
	}
	
	@Override
	public void onRender(Window window, SceneState sceneState) {
		if (sceneState.scene() instanceof ECSScene ecsScene) {
			renderScene(ecsScene);
		}
	}
	
	private void renderScene(ECSScene scene) {
		basicShader.bind();
		
		basicShader.projection.loadMatrix(scene.getProjection());
		basicShader.view.loadMatrix(scene.getView());
		
		var modelCache = scene.getEntityManager().getAddon(ModelCache.class)
				.orElseGet(ModelCache.registerModelCache(scene.getEntityManager()));
		
		scene.getEntityManager().query(Renderable.class).forEach(renderable -> {
			renderEntity(renderable, modelCache);
		});
		
		basicShader.unbind();
	}
	
	private void renderEntity(Renderable entity, ModelCache modelCache) {
		model.identity().translationRotateScale(
				entity.transform.position().x, entity.transform.position().y, entity.transform.position().z,
				entity.transform.orientation().x, entity.transform.orientation().y, entity.transform.orientation().z, entity.transform.orientation().w,
				entity.transform.scale().x, entity.transform.scale().y, entity.transform.scale().z);
		basicShader.model.loadMatrix(model);

		var realModel = modelCache.getModel(entity.modelComponent.model(), "");
		realModel.render();
	}
	
	@Override
	public void onCleanup() {
		basicShader.cleanup();
	}
	
}
