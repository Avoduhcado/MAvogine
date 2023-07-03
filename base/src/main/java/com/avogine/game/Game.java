package com.avogine.game;

import java.util.*;
import java.util.stream.Stream;

import com.avogine.audio.data.AudioSource;
import com.avogine.audio.loader.AudioCache;
import com.avogine.game.scene.Scene;
import com.avogine.game.ui.nuklear.NuklearUI;
import com.avogine.game.util.*;
import com.avogine.io.*;
import com.avogine.io.listener.InputListener;

/**
 * TODO Add an ECSGame subclass or some type of configuration to denote this is an ECS capable Game
 * to avoid the necessity to always cast scene to an ECSScene.
 */
public abstract class Game {
	
	private final Queue<GameListener> registrationQueue;
	private final List<GameListener> gameListeners;
	
	private final List<InputListener> inputListeners;
	
	private final Set<AudioSource> audioSources;
	
	protected Window window;
	protected Audio audio;
	protected NuklearUI gui;
	private Scene scene;
	private Scene nextScene;
	
	protected Game() {
		registrationQueue = new LinkedList<>();
		gameListeners = new ArrayList<>();
		inputListeners = new ArrayList<>();
		audioSources = new HashSet<>();
	}

	/**
	 * Initialize all relevant game logic to start the game loop.
	 * @param window
	 * @param audio 
	 * @param gui
	 */
	public void init(Window window, Audio audio, NuklearUI gui) {
		this.window = window;
		this.audio = audio;
		this.gui = gui;
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
		audioSources.removeIf(AudioSource::isStopped);
		
		var gameState = new GameState(getCurrentScene(), interval);
		getListenersOfType(Updateable.class).forEach(update -> update.onUpdate(gameState));
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
		getListenersOfType(Renderable.class).forEach(render -> render.onRender(sceneState));
		
		gui.render(window);
	}
	
	/**
	 * Free up any allocated memory defined from this object.
	 * <p>
	 * Typically this just consists of calling {@code cleanup()} on any objects that were created
	 * in the {@code init()} method like meshes, shaders, etc.
	 */
	public void cleanup() {
		getListenersOfType(Cleanupable.class).forEach(Cleanupable::onCleanup);
	}
	
	/**
	 * Play an audio file directly in the scene that isn't attached to anything.
	 * TODO Relocate this to Scene? Like in some kind of SoundManager type as well.
	 * @param audioFile
	 * @param loop
	 */
	public void playAudio(String audioFile, boolean loop) {
		var bgmSource = new AudioSource(loop, false);
		audioSources.add(bgmSource);
		var bgmBuffer = AudioCache.getInstance().getSound(audioFile);
		
		bgmSource.setBuffer(bgmBuffer.getBufferID());
		// TODO Use a real gain value
		bgmSource.setGain(0.25f);
		
		bgmSource.play();
	}
	
	/**
	 * Add a {@link GameListener} to the registration queue to be added later.
	 * @param registerable
	 */
	public void register(GameListener registerable) {
		this.registrationQueue.add(registerable);
	}
	
	/**
	 * 
	 */
	public void drainRegistrationQueue() {
		if (nextScene != null) {
			cleanup();
			
			scene = nextScene;
			nextScene = null;
			
			gameListeners.clear();
			
//			window.getInput().removeAllListeners();
			audioSources.forEach(AudioSource::stop);
			audioSources.clear();
			removeSceneInputListeners();
			registrationQueue.clear();
			scene.init(this, window);
		}
		
		while (!registrationQueue.isEmpty()) {
			var registerable = registrationQueue.poll();
			gameListeners.add(registerable);
			registerable.onRegister(this);
		}
	}
	
	protected <T extends GameListener> Stream<T> getListenersOfType(Class<T> clazz) {
		return gameListeners.stream()
				.filter(clazz::isInstance)
				.map(clazz::cast);
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
	
	/**
	 * @return the {@link Audio} instance associated with this {@code Game}.
	 */
	public Audio getAudio() {
		return audio;
	}
	
	/**
	 * @return the {@link NuklearUI} instance associated with this {@code Game}.
	 */
	public NuklearUI getGUI() {
		return gui;
	}
	
}
