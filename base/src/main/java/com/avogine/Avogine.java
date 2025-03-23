package com.avogine;

import com.avogine.game.*;
import com.avogine.io.Window;
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
	
	private Object lock = new Object();
	private boolean destroyed;
	
	/**
	 * @param window The primary game {@link Window} to display to
	 * @param game An implementation of {@link Game} that contains the main game logic
	 */
	public Avogine(Window window, Game game) {
		profiler = FrameProfiler.NO_OP;
		
		this.window = window;
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
			
			synchronized (lock) {
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
		window.init(game.getGLFWConfig(), game.getInputConfig());
	}
	
	private void loop() {
		/*
		 * Start new thread to have the OpenGL context current in and which does
		 * the rendering.
		 */
		new Thread(this::renderLoop).start();
		
		while (!window.shouldClose()) {
			window.waitEvents();
		}
	}
	
	private void input() {
		profiler.inputStart();
		
		game.input(window);
		
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
		
		try {
			timer.syncThreadUntil(endTime);
		} catch (InterruptedException e) {
			AvoLog.log().warn("Render loop sync operation was interrupted.", e);
			Thread.currentThread().interrupt();
		}
	}
	
	private void renderLoop() {
		window.makeCurrent();
		game.baseInit(window);
		
		float updateInterval = 1.0f / game.getTargetUps();
		float updateAccumulator = 0.0f;
		
		var renderTimer = new Timer();
		renderTimer.init();
		
		double loopTime = 0;
		
		while (!destroyed) {
			profiler.startFrame();
			double elapsedTime = renderTimer.getElapsedTime();
			window.setFps((int) (1 / elapsedTime));
			updateAccumulator += elapsedTime;
			
			// TODO This should probably be a stand-alone scheduled repeater
			loopTime += elapsedTime;
			if (loopTime >= 1.0) {
				profiler.printAverages();
				loopTime = 0;
			}
			
			input();

			while (updateAccumulator >= updateInterval) {
				update(updateInterval);
				updateAccumulator -= updateInterval;
			}
			
			render();
			
			profiler.endFrame();
			
			synchronized (lock) {
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
	
	private void cleanup() {
		window.cleanup();
	}
}
