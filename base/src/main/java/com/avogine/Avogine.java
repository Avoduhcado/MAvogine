package com.avogine;

import com.avogine.game.*;
import com.avogine.io.Window;
import com.avogine.io.config.WindowPreferences;
import com.avogine.logging.AvoLog;
import com.avogine.util.FrameProfiler;

/**
 * This is the primary entry point into running a game.
 * <p>
 * To kick off the game loop, create a new {@link Avogine} and supply it with a {@link Window} to render
 * to and a {@link Game} to run, then call {@link #start()}.
 *
 */
public class Avogine {
	
	private final FrameProfiler profiler;
	
	private final Window window;
	private final Game game;
	
	private final Object renderLock = new Object();
	private boolean destroyed;
	
	/**
	 * @param windowTitle 
	 * @param windowPrefs 
	 * @param game An implementation of {@link Game} that contains the main game logic
	 */
	public Avogine(String windowTitle, WindowPreferences windowPrefs, Game game) {
		profiler = FrameProfiler.NO_OP;
		
		window = new Window(windowTitle, windowPrefs, () -> {
			resize();
			return null;
		});
		this.game = game;
	}
	
	/**
	 * Call this method to initialize and begin the game loop.
	 */
	public void start() {
		run();
	}
	
	private void run() {
		try {
			init();
			loop();
			
			synchronized (renderLock) {
				destroyed = true;
				window.close();
			}
		} catch (Exception e) {
			AvoLog.log().error("An error occurred.", e);
			System.exit(-1);
		} finally {
			cleanup();
		}
	}
	
	private void init() {
		window.init();
	}
	
	private void loop() {
		/* Start new thread to have the OpenGL context current in and which does the updating and rendering. */
		new Thread(this::gameLoop, "GAME_LOOP").start();
		
		while (!window.shouldClose()) {
			window.waitEvents();
		}
	}
	
	private void input() {
		profiler.inputStart();
		
		game.input(window);
		window.newInputFrame();
		
		profiler.inputEnd();
	}
	
	private void update(float interval) {
		profiler.updateStart();
		
		game.update(interval);
		
		profiler.updateEnd();
	}
	
	private void render() {
		profiler.renderStart();
		
		game.render(window);
		
		profiler.renderEnd();
	}
	
	private void sync(Timer timer) {
		double targetFrameTime = 1.0 / window.getTargetFps();
		double endTime = timer.getPreviousTime() + targetFrameTime;
		
		timer.syncThreadUntil(endTime);
	}
	
	private void gameLoop() {
		window.makeCurrent();
		game.init(window);
		
		float updateInterval = 1.0f / game.getTargetUps();
		float updateAccumulator = 0.0f;
		
		var renderTimer = new Timer();
		renderTimer.init();
		
		while (!destroyed) {
			profiler.startFrame();
			double elapsedTime = renderTimer.getElapsedTime();
			window.setFps((int) Math.round(1 / elapsedTime));
			updateAccumulator += elapsedTime;
			
			input();
			
			while (updateAccumulator >= updateInterval) {
				update(updateInterval);
				updateAccumulator -= updateInterval;
			}
			
			render();
			
			profiler.endFrame();
			synchronized (renderLock) {
				if (!destroyed) {
					if (!window.isVsync()) {
						// Timer.syncThreadUntil requires GLFW to not be terminated or else the thread will hang, so only sync inside the synchronized block
						sync(renderTimer);
					}
					window.swapBuffers();
				}
			}
			profiler.endBudget();
		}
		
		game.cleanup();
	}
	
	private void resize() {
		int width = window.getWidth();
		int height = window.getHeight();
		game.resize(width, height);
	}
	
	private void cleanup() {
		window.cleanup();
	}
}
