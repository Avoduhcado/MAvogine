package com.avogine.game.util;

import java.util.*;

import com.avogine.game.Game;
import com.avogine.game.scene.Scene;
import com.avogine.io.Window;
import com.avogine.io.listener.InputListener;

/**
 *
 */
public abstract class RegisterableGame implements Game {

	protected final Queue<GameListener> registrationQueue;
	protected final List<Updateable> updateables;
	protected final List<Renderable> renderables;
	protected final List<Cleanupable> cleanupables;
	
	protected final List<InputListener> inputListeners;
	
	protected RegisterableGame() {
		registrationQueue = new LinkedList<>();
		
		updateables = new ArrayList<>();
		renderables = new ArrayList<>();
		cleanupables = new ArrayList<>();
		
		inputListeners = new ArrayList<>();
	}
	
	/**
	 * Add a {@link GameListener} to the registration queue to be added later.
	 * @param listener The {@link GameListener} to be registered.
	 * @return The {@link GameListener} to be registered.
	 */
	public GameListener register(GameListener listener) {
		registrationQueue.add(listener);
		return listener;
	}
	
	/**
	 * 
	 */
	public void drainRegistrationQueue() {
		while (!registrationQueue.isEmpty()) {
			var registerable = registrationQueue.poll();
			
			if (registerable instanceof Updateable updateable) {
				updateables.add(updateable);
			}
			if (registerable instanceof Renderable renderable) {
				renderables.add(renderable);
			}
			if (registerable instanceof Cleanupable cleanupable) {
				cleanupables.add(cleanupable);
			}
			
			registerable.onRegister(this);
		}
	}
	
	/**
	 * Register an {@link InputListener} to the current {@link Window} and store a reference to it for potential de-registering later.
	 * <p>
	 * This should be the preferred way to register {@link Scene} specific {@link InputListener}s to a Window so that {@link RegisterableGame}
	 * can handle unregistering them when the scene changes.
	 * </p>
	 * @param l The {@code InputListener} to add.
	 * @param window The Window to register this listener to.
	 */
	public void addInputListener(InputListener l, Window window) {
		inputListeners.add(window.getInput().addInputListener(l));
	}
	
	/**
	 * Removes all {@link InputListener}s from the current {@link Window} that were attached by the current Scene.
	 */
	protected void removeSceneInputListeners(Window window) {
		for (InputListener l : inputListeners) {
			window.getInput().removeInputListener(l);
		}
	}
	
}
