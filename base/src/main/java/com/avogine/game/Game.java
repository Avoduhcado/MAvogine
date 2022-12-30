package com.avogine.game;

import java.util.*;

import com.avogine.game.scene.Scene;
import com.avogine.game.util.*;
import com.avogine.io.Window;
import com.avogine.io.listener.InputListener;

/**
 *
 */
public abstract class Game {
	
	private final List<Updateable> updateables;
	private final List<Renderable> renderables;
	private final List<Cleanupable> cleanupables;
	
	private final Queue<Registerable> registrationQueue;
	
	private final List<InputListener> inputListeners;
	
	protected Window window;
	private Scene scene;
	private Scene nextScene;
	
	protected Game() {
		updateables = new ArrayList<>();
		renderables = new ArrayList<>();
		cleanupables = new ArrayList<>();
		registrationQueue = new LinkedList<>();
		inputListeners = new ArrayList<>();
	}

	/**
	 * Initialize all relevant game logic to start the game loop.
	 * @param window
	 */
	public void init(Window window) {
		this.window = window;
	}
	
	/**
	 * @param scene the scene to set
	 */
	public void setScene(Scene scene) {
		this.scene = scene;
	}
	
	/**
	 * Return the {@link Scene} that is currently being displayed.
	 * @return The {@code Scene} that is currently being displayed.
	 */
	public Scene getCurrentScene() {
		return scene;
	}

	/**
	 * @param scene
	 */
	public void queueSceneSwap(Scene scene) {
		this.nextScene = scene;
	}
	
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
		scene.prepareRender();
		
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
	 * Add a {@link Registerable} to the registration queue to be added later.
	 * @param registerable
	 */
	public void register(Registerable registerable) {
		this.registrationQueue.add(registerable);
	}
	
	/**
	 * 
	 */
	public void drainRegistrationQueue() {
		if (nextScene != null) {
			scene = nextScene;
			nextScene = null;
			
			this.updateables.clear();
			this.renderables.clear();
			this.cleanupables.clear();
			
//			window.getInput().removeAllListeners();
			removeSceneInputListeners();
			registrationQueue.clear();
			scene.init(this, window);
		}
		
		while (registrationQueue.size() > 0) {
			// I would like to use some sealed interfaces here and a pattern matching switch, but many registerables will implement multiple subtypes
			// and thus cause the switch to skip over things. Resorting to an if block until a better solution presents itself.
			var registerable = registrationQueue.poll();
			if (registerable instanceof Updateable u) {
				addUpdateable(u);
			}
			if (registerable instanceof Renderable r) {
				addRenderable(r);
			}
			if (registerable instanceof Cleanupable c) {
				addCleanupable(c);
			}
		}
	}
	
	/**
	 * Add a new {@link Updateable} object to this {@link Game}.
	 * <p>
	 * {@code Updateable}s linked to this Game will have their {@link Updateable#onUpdate(GameState)} method called once per frame.
	 * @param updateable The {@code Updateable} to add.
	 */
	protected void addUpdateable(Updateable updateable) {
		this.updateables.add(updateable);
		updateable.onRegister(this);
	}
	
	/**
	 * Remove an {@link Updateable} from this {@link Game}.
	 * @param updateable The {@code Updateable} to remove.
	 */
	protected void removeUpdateable(Updateable updateable) {
		this.updateables.remove(updateable);
	}
	
	/**
	 * Add a new {@link Renderable} object to this {@link Game}.
	 * <p>
	 * {@code Renderable}s linked to this Game will have their {@link Renderable#onRender(SceneState)} method called once per frame.
	 * @param renderable The {@code Renderable} to add.
	 */
	protected void addRenderable(Renderable renderable) {
		this.renderables.add(renderable);
		renderable.onRegister(this);
	}
	
	/**
	 * Remove a {@link Renderable} from this {@link Game}.
	 * @param renderable The {@code Renderable} to remove.
	 */
	protected void removeRenderable(Renderable renderable) {
		this.renderables.remove(renderable);
	}
	
	/**
	 * Add a new {@link Cleanupable} object to this {@link Game}.
	 * <p>
	 * {@code Cleanupable}s linked to this Game will have their {@link Cleanupable#onCleanup()} method called once when the game is terminated.
	 * @param cleanupable The {@code Cleanupable} to add.
	 */
	protected void addCleanupable(Cleanupable cleanupable) {
		this.cleanupables.add(cleanupable);
		cleanupable.onRegister(this);
	}
	
	/**
	 * Remove a {@link Cleanupable} from this {@link Game}.
	 * @param cleanupable The {@code Cleanupable} to remove.
	 */
	protected void removeCleanupable(Cleanupable cleanupable) {
		this.cleanupables.remove(cleanupable);
	}
	
	/**
	 * Register an {@link InputListener} to the current {@link Window} and store
	 * a reference to it for potential de-registering later.
	 * @param l The {@code InputListener} to add.
	 */
	public void addInputListener(InputListener l) {
		inputListeners.add(window.getInput().add(l));
	}
	
	/**
	 * Removes all {@link InputListener}s from the current {@link Window} that were attached
	 * by the current {@link Scene}.
	 * </p>
	 * TODO Change {@link Game#inputListeners} into a map of {@code Scene -> List<InputListener>}
	 */
	private void removeSceneInputListeners() {
		for (InputListener l : inputListeners) {
			window.getInput().removeListener(l);
		}
	}
	
	/**
	 * @return the window
	 */
	public Window getWindow() {
		return window;
	}
	
	/**
	 * @param window the window to set
	 */
	public void setWindow(Window window) {
		this.window = window;
	}
	
}
