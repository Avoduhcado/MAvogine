package com.avogine.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL40.GL_DRAW_INDIRECT_BUFFER;

import java.nio.ByteBuffer;
import java.util.*;

import org.lwjgl.opengl.GL43;
import org.lwjgl.system.MemoryUtil;

import com.avogine.entity.Entity;
import com.avogine.game.scene.Scene;
import com.avogine.logging.AvoLog;
import com.avogine.render.data.TextureAtlas;
import com.avogine.render.data.experimental.*;
import com.avogine.render.loader.texture.TextureCache;
import com.avogine.render.shader.ShaderProgram;
import com.avogine.render.shader.uniform.experimental.UniformsMap;

/**
 *
 */
public class SceneRender46 {

	public static final int MAX_DRAW_ELEMENTS = 100;
	public static final int MAX_ENTITIES = 50;
	private static final int COMMAND_SIZE = 5 * 4;
	private static final int MAX_MATERIALS = 20;
	private static final int MAX_TEXTURES = 16;
	
	private Map<String, Integer> entitiesIdxMap;
	
	private SceneShader shader;
	
	private int staticDrawCount;
	private int staticRenderBufferHandle;
	
	private UniformsMap uniformsMap;
	
	/**
	 * 
	 */
	public SceneRender46() {
		shader = new SceneShader();
		createUniforms();
		entitiesIdxMap = new HashMap<>();
	}
	
	private void createUniforms() {
		uniformsMap = new UniformsMap(shader.getProgramId());
		uniformsMap.createUniform("projectionMatrix");
		uniformsMap.createUniform("viewMatrix");
		
		for (int i = 0; i < MAX_TEXTURES; i++) {
			uniformsMap.createUniform("txtSampler[" + i + "]");
		}
		
		for (int i = 0; i < MAX_MATERIALS; i++) {
			String name = "materials[" + i + "]";
			uniformsMap.createUniform(name + ".diffuse");
			uniformsMap.createUniform(name + ".specular");
			uniformsMap.createUniform(name + ".reflectance");
			uniformsMap.createUniform(name + ".normalMapIdx");
			uniformsMap.createUniform(name + ".textureIdx");
		}
		
		for (int i = 0; i < MAX_DRAW_ELEMENTS; i++) {
			String name = "drawElements[" + i + "]";
			uniformsMap.createUniform(name + ".modelMatrixIdx");
			uniformsMap.createUniform(name + ".materialIdx");
		}
		
		for (int i = 0; i < MAX_ENTITIES; i++) {
			uniformsMap.createUniform("modelMatrices[" + i + "]");
		}
	}
	
	public void render(Scene scene, RenderBuffers renderBuffers, GBuffer gBuffer) {
//		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, gBuffer.getGBufferId());
//		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//		glViewport(0, 0, gBuffer.getWidth(), gBuffer.getHeight());
//		glDisable(GL_BLEND);
		
		shader.bind();
		
		uniformsMap.setUniform("projectionMatrix", scene.getProjection());
		uniformsMap.setUniform("viewMatrix", scene.getView());
		
		List<TextureAtlas> textures = TextureCache.getInstance().getAll().stream().toList();
		int numTextures = textures.size();
		if (numTextures > MAX_TEXTURES) {
			AvoLog.log().warn("Only " + MAX_TEXTURES + " textures can be used.");
		}
		for (int i = 0; i < Math.min(MAX_TEXTURES, numTextures); i++) {
			uniformsMap.setUniform("txtSampler[" + i + "]", i);
			var texture = textures.get(i);
			glActiveTexture(GL_TEXTURE0 + i);
			texture.bind();
		}
		
		int entityIdx = 0;
		for (var model : scene.getModelMap46().values()) {
			List<Entity> entities = model.getEntitiesList();
			for (Entity entity : entities) {
				uniformsMap.setUniform("modelMatrices[" + entityIdx + "]", entity.getTransform().modelMatrix());
				entityIdx++;
			}
		}
		
		// Static meshes
		int drawElement = 0;
		for (var model : scene.getModelMap46().values()) {
			List<Entity> entities = model.getEntitiesList();
			for (var meshDrawData : model.getMeshDrawDataList()) {
				for (Entity entity : entities) {
					String name = "drawElements[" + drawElement + "]";
					uniformsMap.setUniform(name + ".modelMatrixIdx", entitiesIdxMap.get(entity.getId()));
					uniformsMap.setUniform(name + ".materialIdx", meshDrawData.materialIdx());
					drawElement++;
				}
			}
		}
		glBindBuffer(GL_DRAW_INDIRECT_BUFFER, staticRenderBufferHandle);
		glBindVertexArray(renderBuffers.getStaticVaoId());
//		GL46.glMultiDrawElementsIndirectCount(GL_TRIANGLES, GL_UNSIGNED_INT, 0, 48, 1024, 0);
		GL43.glMultiDrawElementsIndirect(GL_TRIANGLES, GL_UNSIGNED_INT, 0, staticDrawCount, 0);
		glBindVertexArray(0);
		
//		glEnable(GL_BLEND);
		shader.unbind();
	}
	
	private void setupStaticCommandBuffer(Scene scene) {
		List<AModel46> modelList = scene.getModelMap46().values().stream().toList();
		int numMeshes = 0;
		for (var model : modelList) {
			numMeshes += model.getMeshDrawDataList().size();
		}
		
		int firstIndex = 0;
		int baseInstance = 0;
		ByteBuffer commandBuffer = MemoryUtil.memAlloc(numMeshes * COMMAND_SIZE);
		for (var model : modelList) {
			List<Entity> entities = model.getEntitiesList();
			int numEntities = entities.size();
			for (var meshDrawData : model.getMeshDrawDataList()) {
				// count
				commandBuffer.putInt(meshDrawData.vertices());
				// instanceCount
				commandBuffer.putInt(numEntities);
				commandBuffer.putInt(firstIndex);
				// baseVertex
				commandBuffer.putInt(meshDrawData.offset());
				commandBuffer.putInt(baseInstance);
				
				firstIndex += meshDrawData.vertices();
				baseInstance += entities.size();
			}
		}
		commandBuffer.flip();
		
		staticDrawCount = commandBuffer.remaining() / COMMAND_SIZE;
		
		staticRenderBufferHandle = glGenBuffers();
		glBindBuffer(GL_DRAW_INDIRECT_BUFFER, staticRenderBufferHandle);
		glBufferData(GL_DRAW_INDIRECT_BUFFER, commandBuffer, GL_DYNAMIC_DRAW);
		
		MemoryUtil.memFree(commandBuffer);
	}
	
	private void setupMaterialsUniform(AMaterialCache46 materialCache) {
		List<TextureAtlas> textures = TextureCache.getInstance().getAll().stream().toList();
		int numTextures = textures.size();
		if (numTextures > MAX_TEXTURES) {
			AvoLog.log().warn("Only " + MAX_TEXTURES + " textures can be used.");
		}
//		Map<String, Integer> texturePosMap = new HashMap<>();
//		for (int i = 0; i < Math.min(MAX_TEXTURES, numTextures); i++) {
//			texturePosMap.put(textures.get(i).g, null)
//		}
		
		shader.bind();
		List<AMaterial46> materialList = materialCache.getMaterialsList();
		int numMaterials = materialList.size();
		for (int i = 0; i < numMaterials; i++) {
			var material = materialCache.getMaterial(i);
			String name = "materials[" + i + "]";
			uniformsMap.setUniform(name + ".diffuse", material.getDiffuseColor());
			uniformsMap.setUniform(name + ".specular", material.getSpecularColor());
			uniformsMap.setUniform(name + ".reflectance", material.getReflectance());
			String normalMapPath = material.getNormalMapPath();
			int idx = 0;
//			if (normalMapPath != null) {
//				idx = texturePosMap.computeIfAbsent(normalMapPath, k -> 0);
//			}
			uniformsMap.setUniform(name + ".normalMapIdx", idx);
			uniformsMap.setUniform(name + ".textureIdx", 0);
		}
		shader.unbind();
	}
	
	private void setupEntitiesData(Scene scene) {
		entitiesIdxMap.clear();
		int entityIdx = 0;
		for (var model : scene.getModelMap46().values()) {
			List<Entity> entities = model.getEntitiesList();
			for (Entity entity : entities) {
				entitiesIdxMap.put(entity.getId(), entityIdx);
				entityIdx++;
			}
		}
	}
	
	public void setupData(Scene scene) {
		setupEntitiesData(scene);
		setupStaticCommandBuffer(scene);
		setupMaterialsUniform(scene.getMaterialCache());
	}
	
	public void cleanup() {
		shader.cleanup();
		glDeleteBuffers(staticRenderBufferHandle);
	}
	
	private class SceneShader extends ShaderProgram {
		
		/**
		 * 
		 */
		public SceneShader() {
			super("sceneVertex.glsl", "sceneFragment.glsl");
		}
		
	}
	
}
