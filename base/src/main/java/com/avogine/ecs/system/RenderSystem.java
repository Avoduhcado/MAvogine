package com.avogine.ecs.system;

import org.joml.Matrix4f;

import com.avogine.ecs.Models;
import com.avogine.ecs.components.*;
import com.avogine.ecs.queries.EntityBiQuery;
import com.avogine.game.Game;
import com.avogine.game.scene.ECSScene;
import com.avogine.game.util.*;
import com.avogine.render.shader.BasicShader;

/**
 *
 */
public class RenderSystem implements Renderable, Cleanupable {

	private RenderQuery renderQuery;
	
	private BasicShader basicShader;
	
	@Override
	public void onRegister(Game game) {
		basicShader = new BasicShader("basicVertex.glsl", "basicFragment.glsl");
		renderQuery = new RenderQuery(basicShader);
	}

	@Override
	public void onRender(SceneState sceneState) {
		if (sceneState.scene() instanceof ECSScene scene) {
			render(scene);
		}
	}
	
	private void render(ECSScene scene) {
		basicShader.bind();
		
		basicShader.projection.loadMatrix(scene.getProjection());
		basicShader.view.loadMatrix(scene.getView());
		
		scene.getEntityManager().queryAndProcess(renderQuery);
		
		basicShader.unbind();
	}

	@Override
	public void onCleanup() {
		basicShader.cleanup();
	}
	
	private static class RenderQuery extends EntityBiQuery<TransformComponent, ModelComponent> {

		private final Matrix4f modelMatrix;
		
		private BasicShader basicShader;
		
		/**
		 * @param basicShader 
		 */
		public RenderQuery(BasicShader basicShader) {
			modelMatrix = new Matrix4f();
			this.basicShader = basicShader;
		}
		
		@Override
		public void accept(TransformComponent transform, ModelComponent model) {
			modelMatrix.identity().translationRotateScale(
					transform.x(), transform.y(), transform.z(),
					transform.rx(), transform.ry(), transform.rz(), transform.rw(),
					transform.sx(), transform.sy(), transform.sz());
			basicShader.model.loadMatrix(modelMatrix);
			
			Models.CACHE.getModel(model.modelName(), "").render();
		}
		
	}

}
