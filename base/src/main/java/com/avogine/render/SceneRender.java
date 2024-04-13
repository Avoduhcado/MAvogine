package com.avogine.render;

import java.util.*;

import org.joml.*;
import org.lwjgl.opengl.*;

import com.avogine.entity.Entity;
import com.avogine.game.Game;
import com.avogine.game.util.*;
import com.avogine.render.data.experimental.*;
import com.avogine.render.loader.texture.TextureCache;
import com.avogine.render.shader.BasicShader;

/**
 *
 */
public class SceneRender implements Renderable, Cleanupable {

	private BasicShader basicShader;
	
	@Override
	public void onRegister(Game game) {
		this.basicShader = new BasicShader("basicVertex.glsl", "basicFragment.glsl");
	}

	@Override
	public void onRender(SceneState sceneState) {
		var projectionView = sceneState.scene().getProjection().mul(sceneState.scene().getView(), new Matrix4f());
		var frustum = new FrustumIntersection(projectionView);
		
		basicShader.bind();
		
		basicShader.projection.loadMatrix(sceneState.scene().getProjection());
		basicShader.view.loadMatrix(sceneState.scene().getView());
		
		Collection<AModel> models = sceneState.scene().getModelMap().values();
		for (AModel model : models) {
			List<Entity> entities = model.getEntities();
			
			for (AMaterial material : model.getMaterials()) {
				// TODO Bind materials in shader
				if (material.getDiffuseTexturePath() != null) {
					TextureCache.getInstance().getTexture(material.getDiffuseTexturePath()).bind();
				}
				
				for (AMesh mesh : material.getMeshes()) {
					GL30.glBindVertexArray(mesh.getVao());
					entities.stream()
					.filter(entity -> frustum.testSphere(entity.getTransform().translation(), 5))
					.forEach(entity -> {
						basicShader.model.loadMatrix(entity.getTransform().modelMatrix());
						GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVerticesLength(), GL11.GL_UNSIGNED_INT, 0);
					});
				}
			}
		}
		
		GL30.glBindVertexArray(0);
		basicShader.unbind();
	}

	@Override
	public void onCleanup() {
		if (basicShader != null) {
			basicShader.cleanup();
		}
	}
	
}
