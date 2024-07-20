package com.avogine.render;

import static org.lwjgl.opengl.GL11.*;

import java.util.*;

import org.lwjgl.opengl.GL11;

import com.avogine.game.scene.SwappableScene;
import com.avogine.game.util.Renderable;
import com.avogine.io.Window;

/**
 * TODO Should this be abstract? It's primarily just a {@link Renderable} container so may work fine as is.
 */
public class HotRender implements SceneRender<SwappableScene> {

	private final List<Renderable> renderables;
	
	/**
	 * 
	 */
	public HotRender() {
		renderables = new ArrayList<>();
	}
	
	@Override
	public void render(Window window, SwappableScene scene) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
		
		var sceneState = scene.getSceneState();
		renderables.forEach(render -> render.onRender(window, sceneState));
	}

	/**
	 * @param renderable
	 * @return
	 */
	public Renderable registerRenderable(Renderable renderable) {
		renderables.add(renderable);
		return renderable;
	}

	@Override
	public void cleanup() {
		renderables.clear();
	}
	
}
