package com.avogine.game;

import java.util.*;

import com.avogine.audio.data.AudioSource;
import com.avogine.audio.loader.AudioCache;
import com.avogine.game.scene.*;
import com.avogine.game.ui.nuklear.NuklearUI;
import com.avogine.game.util.*;
import com.avogine.io.*;
import com.avogine.io.listener.InputListener;
import com.avogine.render.HotRender;

/**
 * A {@link Game} implementation that allows dynamic processing of {@link GameListener}s.
 * <p>
 * The Hot prefix denotes "Hot Swappable" and naming is subject to change once I find a suitable use case for implementing this Game type.
 * Currently, the idea allows implementors to control state through initializing different implementations of {@link SwappableScene} whenever a
 * major context switch occurs in the game logic, such as going from a title screen into an actual playable level so as to reduce clutter in your
 * Game type.
 * </p>
 * TODO Add an ECSGame subclass or some type of configuration to denote this is an ECS capable Game to avoid the necessity to always cast scene to an ECSScene.
 */
public abstract class HotGame implements Game {

	/**
	 * Default number of updates per second.
	 * <p>
	 * Controls how often game logic is run in a single second.
	 */
	private static final int TARGET_UPS = 60;
	
	private final Queue<GameListener> registrationQueue;
	private final List<Updateable> updateables;
	private final List<Cleanupable> cleanupables;
	
	protected final HotRender render;
	
	private final List<InputListener> inputListeners;
	
	// TODO Move this to a cache in Audio or something
	private final Set<AudioSource> audioSources;
	
	protected SwappableScene scene;
	// TODO This could be made into a Queue, but I'm unsure if that is necessary or makes sense. Scene swaps should not occur very incredibly often.
	private SwappableScene nextScene;
	
	protected Audio audio;
	protected NuklearUI gui;

	private int targetUps;
	
	protected HotGame(SwappableScene scene) {
		registrationQueue = new LinkedList<>();
		updateables = new ArrayList<>();
		cleanupables = new ArrayList<>();
		inputListeners = new ArrayList<>();
		audioSources = new HashSet<>();
		targetUps = TARGET_UPS;
		
		this.scene = scene;
		render = new HotRender();
	}
	
	/**
	 * Return the {@link Scene} that is currently being played.
	 * @return The {@code Scene} that is currently being played.
	 */
	public Scene getCurrentScene() {
		return scene;
	}

	/**
	 * @param scene
	 */
	public void queueSceneSwap(SwappableScene scene) {
		this.nextScene = scene;
	}
	
	@Override
	public void input(Window window) {
		// TODO Where should this go?
		drainRegistrationQueue(window);
		
		if (gui != null) {
			gui.inputBegin();
		}
		
		window.pollEvents();
		
		if (gui != null) {
			gui.inputEnd();
		}
	}
	
	@Override
	public void update(float interval) {
		audioSources.removeIf(AudioSource::isStopped);
		
		var gameState = new GameState(getCurrentScene(), interval);
		updateables.forEach(update -> update.onUpdate(gameState));
	}
	
	@Override
	public void render(Window window) {
		render.render(window, scene);
	}
	
	@Override
	public void cleanup() {
		cleanupables.forEach(Cleanupable::onCleanup);
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
	 * @return The {@link GameListener} to be registered.
	 */
	public GameListener register(GameListener registerable) {
		registrationQueue.add(registerable);
		return registerable;
	}
	
	/**
	 * @param window 
	 */
	public void drainRegistrationQueue(Window window) {
		if (nextScene != null) {
			scene = nextScene;
			nextScene = null;
			
			cleanup();
			updateables.clear();
			render.cleanup();
			
			audioSources.forEach(AudioSource::stop);
			audioSources.clear();
			removeSceneInputListeners(window);
			registrationQueue.clear();
		}
		
		while (!registrationQueue.isEmpty()) {
			var registerable = registrationQueue.poll();
			
			if (registerable instanceof Updateable updateable) {
				updateables.add(updateable);
			}
			if (registerable instanceof Renderable renderable) {
				render.registerRenderable(renderable);
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
	 * This should be the preferred way to register {@link SwappableScene} specific {@link InputListener}s to a Window so that {@link HotGame}
	 * can handle unregistering them when the scene changes.
	 * </p>
	 * @param l The {@code InputListener} to add.
	 * @param window The Window to register this listener to.
	 */
	public void addInputListener(InputListener l, Window window) {
		inputListeners.add(window.getInput().addInputListener(l));
	}
	
	/**
	 * Removes all {@link InputListener}s from the current {@link Window} that were attached by the current {@link SwappableScene}.
	 */
	private void removeSceneInputListeners(Window window) {
		for (InputListener l : inputListeners) {
			window.getInput().removeInputListener(l);
		}
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
	
	@Override
	public int getTargetUps() {
		return targetUps;
	}
	
	/**
	 * @param targetUps the targetUps to set
	 */
	public void setTargetUps(int targetUps) {
		this.targetUps = targetUps;
	}
	
}
