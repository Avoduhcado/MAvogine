package com.avogine.game;

import java.util.*;

import com.avogine.game.scene.*;
import com.avogine.game.util.*;
import com.avogine.io.*;

/**
 *
 */
public abstract class Game {
	
	private final List<Updateable> updateables;
	private final List<Renderable> renderables;
	private final List<Cleanupable> cleanupables;
	
	protected Game() {
		updateables = new ArrayList<>();
		renderables = new ArrayList<>();
		cleanupables = new ArrayList<>();
	}

	/**
	 * Initialize all relevant game logic to start the game loop.
	 * @param window
	 */
	public abstract void init(Window window);
	
	/**
	 * Return the {@link Scene} that is currently being displayed.
	 * @return The {@code Scene} that is currently being displayed.
	 */
	public abstract Scene getCurrentScene();
	
	/**
	 * Update any relevant entities or systems at a fixed time step determined by {@code interval}.
	 * @param interval The time elapsed between updates in seconds
	 */
	public void update(float interval) {
		var gameState = new GameState(getCurrentScene(), interval);
		updateables.forEach(update -> update.onUpdate(gameState));
	}
	
	/**
	 * Draw the current scene.
	 * <p>
	 * This should be called exactly once per frame.
	 * <p>
	 * <b>XXX Should explicit calls to GL methods be avoided from this implementation? And be dependent on something deeper? I don't
	 * particularly care about abstract rendering methods at the moment.</b>
	 */
	public void render() {
		var sceneState = new SceneState(getCurrentScene());
		renderables.forEach(render -> render.onRender(sceneState));
	}
	
	/**
	 * Free up any allocated memory defined from this object.
	 * <p>
	 * Typically this just consists of calling {@code cleanup()} on any objects that were created
	 * in the {@code init()} method like meshes, shaders, etc.
	 */
	public void cleanup() {
		cleanupables.forEach(Cleanupable::onCleanup);
	}
	
	/**
	 * Add a new {@link Updateable} object to this {@link Game}.
	 * <p>
	 * {@code Updateable}s linked to this Game will have their {@link Updateable#onUpdate(GameState)} method called once per frame.
	 * @param updateable The {@code Updateable} to add.
	 */
	public void addUpdateable(Updateable updateable) {
		this.updateables.add(updateable);
	}
	
	/**
	 * Remove an {@link Updateable} from this {@link Game}.
	 * @param updateable The {@code Updateable} to remove.
	 */
	public void removeUpdateable(Updateable updateable) {
		this.updateables.remove(updateable);
	}
	
	/**
	 * Add a new {@link Renderable} object to this {@link Game}.
	 * <p>
	 * {@code Renderable}s linked to this Game will have their {@link Renderable#onRender(SceneState)} method called once per frame.
	 * @param renderable The {@code Renderable} to add.
	 */
	public void addRenderable(Renderable renderable) {
		this.renderables.add(renderable);
	}
	
	/**
	 * Remove a {@link Renderable} from this {@link Game}.
	 * @param renderable The {@code Renderable} to remove.
	 */
	public void removeRenderable(Renderable renderable) {
		this.renderables.remove(renderable);
	}
	
	/**
	 * Add a new {@link Cleanupable} object to this {@link Game}.
	 * <p>
	 * {@code Cleanupable}s linked to this Game will have their {@link Cleanupable#onCleanup()} method called once when the game is terminated.
	 * @param cleanupable The {@code Cleanupable} to add.
	 */
	public void addCleanupable(Cleanupable cleanupable) {
		this.cleanupables.add(cleanupable);
	}
	
	/**
	 * Remove a {@link Cleanupable} from this {@link Game}.
	 * @param cleanupable The {@code Cleanupable} to remove.
	 */
	public void removeCleanupable(Cleanupable cleanupable) {
		this.cleanupables.remove(cleanupable);
	}
	
}
