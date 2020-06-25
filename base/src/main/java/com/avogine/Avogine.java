package com.avogine;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import com.avogine.game.Game;
import com.avogine.game.Timer;
import com.avogine.io.Input;
import com.avogine.io.Window;
import com.avogine.logging.LogUtil;

/**
 * This is the primary entry point into running a game.
 * <p>
 * To kick off the game loop, create a new {@link Avogine} and supply it with a {@link Window} to render
 * to and a {@link Game} to run.
 * @author Dominus
 *
 */
public class Avogine implements Runnable {

	private static final Logger logger = LogUtil.requestLogger(Avogine.class);

	/**
	 * Target frames per second. Controls how often the screen is rendered in a single second.
	 */
	public static final int TARGET_FPS = 60;

	/**
	 * Target updates per second. Controls how often game logic is run in a single second.
	 */
	public static final int TARGET_UPS = 60;

	private final Thread gameLoopThread;
	
	// TODO Better Theater/Window and Stage hierarchy
	private final Window window;
	private final Game game;
	
	private final Timer timer;
	// TODO Add an engine level Input class, similar to Timer, to handle all incoming Inputs through Window and route them to game logic in Game
	private final Input input;
	
	private float frameTime;
	private int fps;
	
	/**
	 * @param window
	 * @param game 
	 */
	public Avogine(Window window, Game game) {
		gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
		this.window = window;
		this.game = game;
		timer = new Timer();
		input = new Input();
	}
	
	/**
	 * Call this method to begin the game loop.
	 * <p>
	 * This has minor tweaks for running threads on Mac, namely setting the System property "java.awt.headless" to
	 * true to avoid conflicts between GLFW and AWT.
	 */
	public void start() {
		String osName = System.getProperty("os.name");
		if (osName.contains("Mac")) {
			System.setProperty("java.awt.headless", "true");
			logger.info("Setting java.awt.headless to true");
			gameLoopThread.run();
		} else {
			gameLoopThread.start();
		}
	}
	
	@Override
	public void run() {
		try {
			init();
			loop();
		} catch (Exception e) {
			logger.error("An error occurred.", e);
		} finally {
			cleanup();
		}
	}
	
	private void init() {
		window.init();
		input.init(window);
		// TODO I don't like this
		game.init(window, input);
		timer.init();
		
		// XXX This sort of works, but only when the window is resized, not while it's being moved/held
//		GLFW.glfwSetWindowRefreshCallback(window.getId(), (window) -> {
//			logger.info("Refreshing");
//			
//			update(0.045f);
//			
//			fps++;
//			if (frameTime >= 1.0f) {
//				window.setFps(fps);
//				frameTime = 0;
//				fps = 0;
//			}
//			stage.render(window);
//			
//			GLFW.glfwSwapBuffers(window);
//		});
		
	}
	
	/**
	 * Call once to kick off game loop.
	 * <p>
	 * Runs as long as {@link #window} is not set to close as specified by {@link GLFW#glfwWindowShouldClose(long)}.
	 * In order, once per frame the {@link #frameTime} is calculated, input is processed, update code is run, the screen is rendered, and then also synced.
	 */
	private void loop() {
		float elapsedTime;
		float accumulator = 0f;
		float interval = 1f / TARGET_UPS;

		boolean running = true;
		while (running && !window.shouldClose()) {
			// XXX Minor hack to limit frame updates when the window is "held" and instead tie pauses to at least the target UPS
			elapsedTime = Math.min(interval, timer.getElapsedTime());
			accumulator += elapsedTime;
			frameTime += elapsedTime;

			input(window);

			while (accumulator >= interval) {
				update(interval);
				accumulator -= interval;
			}
			
			render();

			sync();
		}
	}
	
	private void input(Window window) {
		// TODO Change this to call Input.update() over a collection of Theaters
		input.update(window);
//		stage.input(window);
	}
	
	private void render() {
		fps++;
		if (frameTime >= 1.0f) {
			window.setFps(fps);
			frameTime = 0;
			fps = 0;
		}
		game.render();
		window.update();
	}
	
	private void update(float interval) {
		// TODO Process EventQueue
		game.update(interval);
	}
	
	private void sync() {
		float loopSlot = 1f / TARGET_FPS;
		double endTime = timer.getLastLoopTime() + loopSlot;
		while (timer.getTime() < endTime) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException ie) {
			}
		}
	}
	
	private void cleanup() {
		game.cleanup();
		
		Callbacks.glfwFreeCallbacks(window.getId());
		GLFW.glfwDestroyWindow(window.getId());
		
		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
	}

}
