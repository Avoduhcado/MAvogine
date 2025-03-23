package com.avogine.render;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL14C.glBlendEquation;

import org.joml.Matrix4f;
import org.lwjgl.opengl.*;

import com.avogine.game.ui.NuklearGUI;
import com.avogine.io.Window;
import com.avogine.render.data.nuklear.NuklearMesh;
import com.avogine.render.shader.NuklearShader;

/**
 *
 */
public class NuklearRender {

	private final Matrix4f projectionMatrix;

	private NuklearShader nuklearShader;

	private NuklearMesh mesh;
	
	private int displayWidth;
	private int displayHeight;
	
	/**
	 * TODO Initialize a singular NkContext for all Nuklear operations, but create a unique NkBuffer for each distinct UI element? ie. Title Menu, in-game Menu 
	 */
	public NuklearRender() {
		projectionMatrix = new Matrix4f();
	}
	
	/**
	 * @param window
	 */
	public void init(Window window) {
		displayWidth = window.getWidth();
		displayHeight = window.getHeight();

		mesh = new NuklearMesh(displayWidth, displayHeight, displayWidth, displayHeight);
		
		projectionMatrix.ortho2D(0, displayWidth, displayHeight, 0);

		nuklearShader = new NuklearShader();
	}

	/**
	 * @param nuklearContext 
	 */
	public void render(NuklearGUI nuklearContext) {
		setupUIState();
		glViewport(0, 0, displayWidth, displayHeight);
		
		nuklearShader.bind();
		nuklearShader.projectionMatrix.loadMatrix(projectionMatrix);
		
		mesh.prepareCommandQueue(nuklearContext.getContext(), nuklearContext.getCommands());
		
		nuklearShader.unbind();
		
		teardownUIState();
	}
	
	private void setupUIState() {
		// setup global state
		glEnable(GL_BLEND);
		glBlendEquation(GL14.GL_FUNC_ADD);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST);
		glEnable(GL_SCISSOR_TEST);
		glActiveTexture(GL13.GL_TEXTURE0);
	}
	
	private void teardownUIState() {
		glDisable(GL_BLEND);
		glDisable(GL_SCISSOR_TEST);
		// XXX Re-enable these based on some global render settings or cache the values beforehand?
		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
	}
	
	/**
	 * 
	 */
	public void cleanup() {
		nuklearShader.cleanup();
		mesh.cleanup();
	}
	
	/**
	 * @param width
	 * @param height
	 */
	public void resize(int width, int height) {
		this.displayWidth = width;
		this.displayHeight = height;
		
		mesh.setSize(width, height);
		
		projectionMatrix.setOrtho2D(0, displayWidth, displayHeight, 0);
	}
	
}
