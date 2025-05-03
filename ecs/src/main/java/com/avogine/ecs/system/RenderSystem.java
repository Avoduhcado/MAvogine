package com.avogine.ecs.system;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.UUID;

import org.joml.Matrix4f;

import com.avogine.ecs.*;
import com.avogine.ecs.addons.ModelCache;
import com.avogine.ecs.components.*;
import com.avogine.game.scene.*;
import com.avogine.game.util.*;
import com.avogine.io.Window;
import com.avogine.render.data.mesh.StaticMesh;
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
	public void onRegister(RegisterableGame game) {
		basicShader = new BasicShader();
	}
	
	@Override
	public void onRender(Window window, Scene scene) {
		if (scene instanceof ECSScene ecsScene) {
			renderScene(ecsScene);
		}
	}
	
	private void renderScene(ECSScene scene) {
		basicShader.bind();
		
		basicShader.projection.loadMatrix(scene.getProjectionMatrix());
		basicShader.view.loadMatrix(scene.getViewMatrix());
		
		var modelCache = scene.getEntityManager().getAddon(ModelCache.class)
				.orElseGet(ModelCache.registerModelCache(scene.getEntityManager()));
		
		scene.getEntityManager().query(Renderable.class).forEach(renderable -> renderEntity(renderable, modelCache));
		
		basicShader.unbind();
	}
	
	private void renderEntity(Renderable entity, ModelCache modelCache) {
		var realModel = modelCache.getModel(entity.modelComponent.model(), "");
		realModel.getMaterials().forEach(material -> {
			glActiveTexture(GL_TEXTURE0);
			material.getDiffuseTexture().bind();
			
			realModel.getMeshes().stream()
			.filter(StaticMesh.class::isInstance)
			.map(StaticMesh.class::cast)
			.filter(mesh -> mesh.getMaterialIndex() == realModel.getMaterials().indexOf(material))
			.forEach(mesh -> {
				glBindVertexArray(mesh.getVaoId());

				model.identity().translationRotateScale(
						entity.transform.position().x, entity.transform.position().y, entity.transform.position().z,
						entity.transform.orientation().x, entity.transform.orientation().y, entity.transform.orientation().z, entity.transform.orientation().w,
						entity.transform.scale().x, entity.transform.scale().y, entity.transform.scale().z);
				basicShader.model.loadMatrix(model);
				
				glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);
			});
		});
		
		glBindVertexArray(0);
	}
	
	@Override
	public void onCleanup() {
		basicShader.cleanup();
	}
	
}
