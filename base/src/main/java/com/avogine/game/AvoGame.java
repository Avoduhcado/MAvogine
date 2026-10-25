package com.avogine.game;

import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.opengl.GL11C.*;

import org.lwjgl.opengl.GL;

import com.avogine.audio.Audio;
import com.avogine.game.state.GameState;
import com.avogine.game.ui.NuklearGUI;
import com.avogine.io.Window;

/**
 * {@link Game} implementation configured to provide a {@link NuklearGUI}, {@link Audio} and set up OpenGL automatically.
 * @param <T> 
 */
public abstract class AvoGame<T extends GameState<?, ?>> implements Game {
	protected T state;
	
	protected final NuklearGUI gui;
	protected final Audio audio;
	
	protected AvoGame() {
		gui = new NuklearGUI();
		audio = new Audio();
	}
	
	@Override
	public final void init(Window window) {
		GL.createCapabilities();
		// Set a nice Avocado green default color
		glClearColor(177f / 255f, 193f / 255f, 71f / 255f, 1.0f);
		// GLFW window settings
		glfwSwapInterval(window.isVsync() ? 1 : 0);
		
		gui.init(window);
		
		// OpenAL base capabilities and settings
		audio.init();
		
		onInit(window);
	}
	
	protected abstract void onInit(Window window);
	
	@Override
	public void cleanup() {
		onCleanup();
		
		gui.cleanup();
		audio.cleanup();
	}
	
	/**
	 * This is run <i>before</i> the GUI and Audio cleanup to avoid resource collisions when freeing.
	 */
	protected abstract void onCleanup();
	
	@Override
	public final void input(Window window) {
		gui.input(window);
		gui.layoutElements(window);
		
		onInput(window);
	}
	
	protected abstract void onInput(Window window);
	
	@Override
	public final void render(Window window) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glViewport(0, 0, window.getWidth(), window.getHeight());
		
		onRender(window);
	}
	
	protected abstract void onRender(Window window);
	
	/**
	 * @return the current state
	 */
	public T getState() {
		return state;
	}
}
