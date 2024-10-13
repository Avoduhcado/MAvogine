package com.avogine.game;

import java.util.*;

import com.avogine.audio.data.AudioSource;
import com.avogine.audio.loader.AudioCache;
import com.avogine.game.scene.Scene;
import com.avogine.game.state.*;
import com.avogine.game.ui.nuklear.NuklearUI;
import com.avogine.game.util.*;
import com.avogine.io.*;
import com.avogine.util.Result;

/**
 * A {@link Game} implementation that allows dynamic processing of {@link GameListener}s.
 * <p>
 * The Hot Swap prefix denotes "Hot Swappable" and naming is subject to change once I find a suitable use case for implementing this Game type.
 * Currently, the idea allows implementors to control state through initializing different implementations of {@link GameState} whenever a
 * major context switch occurs in the game logic, such as going from a title screen into an actual playable level so as to reduce clutter in your
 * Game type.
 * </p>
 * TODO Add an ECSGame subclass or some type of configuration to denote this is an ECS capable Game to avoid the necessity to always cast scene to an ECSScene.
 */
public abstract class HotSwapGame extends RegisterableGame implements StateSwappable<GameState<?,?>> {

	/**
	 * Default number of updates per second.
	 * <p>
	 * Controls how often game logic is run in a single second.
	 */
	private static final int TARGET_UPS = 60;
	
	// TODO Move this to a cache in Audio or something
	protected final Set<AudioSource> audioSources;
	
	protected GameState<?, ?> gameState;
	protected Class<? extends GameState<? ,?>> queuedGameStateClass;
	
	protected Audio audio;
	protected NuklearUI gui;

	private int targetUps;
	
	protected HotSwapGame(GameState<?, ?> gameState) {
		super();
		audioSources = new HashSet<>();
		targetUps = TARGET_UPS;
		
		this.gameState = gameState;
	}
	
	/**
	 * Return the {@link Scene} that is currently being played.
	 * @return The {@code Scene} that is currently being played.
	 */
	public Scene getCurrentScene() {
		return gameState.getScene();
	}

	@Override
	public void input(Window window) {
		if (Objects.nonNull(queuedGameStateClass)) {
			swapGameState(window);
		}
		drainRegistrationQueue();
	}
	
	@Override
	public void update(float interval) {
		audioSources.removeIf(AudioSource::isStopped);
		
		updateables.forEach(update -> update.onUpdate(getCurrentScene(), interval));
	}
	
	@Override
	public void render(Window window) {
		renderables.forEach(render -> render.onRender(window, getCurrentScene()));
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
	
	@Override
	public void queueGameState(Class<GameState<?, ?>> gameStateClass) {
		Objects.requireNonNull(gameStateClass);
		
		queuedGameStateClass = gameStateClass;
	}
	
	@Override
	public void swapGameState(Window window) {
		var swappedGameState = Result.runCatching(() -> queuedGameStateClass.getDeclaredConstructor().newInstance()).getOrNull();
		queuedGameStateClass = null;
		Objects.requireNonNull(swappedGameState);
		
		cleanup();
		updateables.clear();
		renderables.clear();
		
		audioSources.forEach(AudioSource::stop);
		audioSources.clear();
		removeSceneInputListeners(window);
		registrationQueue.clear();
		
		gameState.cleanup();
		gameState = swappedGameState;
		gameState.init(window);
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
