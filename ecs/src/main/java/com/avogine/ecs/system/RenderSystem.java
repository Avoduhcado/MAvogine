package com.avogine.ecs.system;

import java.util.*;
import java.util.stream.Collectors;

import org.joml.*;
import org.lwjgl.opengl.*;

import com.avogine.ecs.*;
import com.avogine.ecs.addons.ModelCache;
import com.avogine.ecs.components.*;
import com.avogine.game.Game;
import com.avogine.game.scene.ECSScene;
import com.avogine.game.util.*;
import com.avogine.render.data.material.Material;
import com.avogine.render.data.mesh.Mesh;
import com.avogine.render.shader.BasicShader;

/**
 *
 */
public class RenderSystem extends EntitySystem implements Renderable, Cleanupable {

	private record Renderable(UUID id, TransformComponent transform, ModelComponent modelComponent) implements EntityArchetype {}
	private record RenderBatch(List<TransformComponent> transforms) {}
	
	private BasicShader basicShader;
	
	private final Matrix4f model;
	
	private final Set<Class<? extends EntityComponent>> archetype;
	
	/**
	 * 
	 */
	public RenderSystem() {
		model = new Matrix4f();
		archetype = Set.of(TransformComponent.class, ModelComponent.class);
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
		var projectionView = scene.getProjection().mul(scene.getView(), new Matrix4f());
		var frustum = new FrustumIntersection(projectionView);
		
		basicShader.bind();
		
		basicShader.projection.loadMatrix(scene.getProjection());
		basicShader.view.loadMatrix(scene.getView());
		
		var modelCache = scene.getEntityManager().getAddon(ModelCache.class)
				.orElseGet(ModelCache.registerModelCache(scene.getEntityManager()));
		
		mapToRender(scene, modelCache, frustum);
//		queryToMap(scene, modelCache, frustum);
		
//		scene.getEntityManager().query(Renderable.class).forEach(renderable -> {
//			renderEntity(renderable, modelCache);
//		});
		
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
	
	private void mapToRender(ECSScene scene, ModelCache modelCache, FrustumIntersection frustum) {
		var renders = scene.getEntityManager().query2(archetype)
				.map(entity -> {
					var map = entity.getValue();
					if (map.get(TransformComponent.class) instanceof TransformComponent t 
							&& map.get(ModelComponent.class) instanceof ModelComponent m) {
						return new Renderable(entity.getKey(), t, m);
					}
					return null;
				})
				.filter(Objects::nonNull)
				.collect(Collectors.groupingBy(e -> e.modelComponent));
		renders.entrySet().forEach(r -> {
			var entities = r.getValue();
			var realModel = modelCache.getModel(r.getKey().model(), "");

			for (Material m : realModel.getMaterials()) {
				m.bind();

				for (Mesh mesh : realModel.getMeshes()) {
					GL30.glBindVertexArray(mesh.getVao());
					entities.stream()
					.filter(entity -> frustum.testSphere(entity.transform.position(), 5))
					.forEach(entity -> {
						model.identity().translationRotateScale(
								entity.transform.position(), entity.transform.orientation(), entity.transform.scale());
						basicShader.model.loadMatrix(model);
						GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
					});
				}
			}
		});
	}
	
	private void queryToMap(ECSScene scene, ModelCache modelCache, FrustumIntersection frustum) {
		Map<ModelComponent, RenderBatch> temp2 = scene.getEntityManager().query2(archetype)
				.collect(Collectors.groupingBy(
						map -> {
							if (map.getValue().get(ModelComponent.class) instanceof ModelComponent m) {
								return m;
							}
							return null;
						},
						Collectors.collectingAndThen(Collectors.toList(), list -> {
							var tList = list.stream()
									.map(map -> {
										if (map.getValue().get(TransformComponent.class) instanceof TransformComponent t) {
											return t;
										}
										return null;
									})
									.filter(Objects::nonNull)
									.toList();
							return new RenderBatch(tList);
						})));
		temp2.entrySet().forEach(r -> {
			var entities = r.getValue();
			var realModel = modelCache.getModel(r.getKey().model(), "");

			for (Material m : realModel.getMaterials()) {
				m.bind();

				for (Mesh mesh : realModel.getMeshes()) {
					GL30.glBindVertexArray(mesh.getVao());
					// GL43.glMultiDrawElementsIndirect(GL11.GL_TRIANGLES, GL11.GL_UNSIGNED_INT, (int[]) null, entities.size(), 0);
					entities.transforms.forEach(entity -> {
						model.identity().translationRotateScale(
								entity.position(), entity.orientation(), entity.scale());
						basicShader.model.loadMatrix(model);
						GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
					});
				}
			}
		});
	}
	
	@Override
	public void onCleanup() {
		basicShader.cleanup();
	}
	
}
