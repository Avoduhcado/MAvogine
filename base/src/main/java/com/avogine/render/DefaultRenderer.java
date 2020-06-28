/**
 * 
 */
package com.avogine.render;

import java.util.Map.Entry;
import java.util.Set;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import com.avogine.entity.Entity;
import com.avogine.game.scene.DefaultScene3D;
import com.avogine.render.data.Mesh;
import com.avogine.render.shader.SimpleShader;

/**
 *
 */
public class DefaultRenderer implements Renderer<DefaultScene3D> {
	
	private SimpleShader simple;
	
	@Override
	public void init() {
		simple = new SimpleShader("simpleVertex.glsl", "simpleFragment.glsl");
	}
	
	@Override
	public void renderScene(DefaultScene3D scene, Matrix4f projection, Matrix4f view) {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		simple.bind();
		
		simple.projectionMatrix.loadMatrix(projection);
		simple.viewMatrix.loadMatrix(view);

		simple.lightPosition.loadVec3(-10, 10, 0);
		simple.lightColor.loadVec3(1f, 1f, 1f);

		simple.useColor.loadInteger(1);
		for (Entry<Mesh, Set<Entity>> entry : scene.getMeshMap().entrySet()) {
			// TODO Load per mesh material
			entry.getKey().renderBatch(entry.getValue(), (entity -> {
				Matrix4f modelMatrix = new Matrix4f();
				modelMatrix.translate(entity.getPosition());
				simple.modelMatrix.loadMatrix(modelMatrix);
			}));
		}
		
		simple.unbind();
	}
	
	@Override
	public void cleanup() {
		simple.cleanup();
	}

}
