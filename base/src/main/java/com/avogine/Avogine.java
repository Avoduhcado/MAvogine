package com.avogine;

import java.lang.invoke.MethodHandles;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avogine.game.Game;
import com.avogine.game.Timer;
import com.avogine.io.Input;
import com.avogine.io.Window;

/**
 * This is the primary entry point into running a game.
 * <p>
 * To kick off the game loop, create a new {@link Avogine} and supply it with a {@link Window} to render
 * to and a {@link Game} to run, then call {@link #start()}.
 * @author Dominus
 *
 */
public class Avogine implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

	/**
	 * TODO This needs to be customizable/read from a property.
	 * Target frames per second. Controls how often the screen is rendered in a single second.
	 */
	public static final int TARGET_FPS = 144;

	/**
	 * Target updates per second. Controls how often game logic is run in a single second.
	 */
	public static final int TARGET_UPS = TARGET_FPS;

	private final Thread gameLoopThread;
	
	private final Window window;
	private final Game game;
	
	private final Timer timer;
	private final Input input;
	
	private float frameTime;
	private int fps;
	private float sleepRemainder;
	
	/**
	 * @param window The primary game {@link Window} to display to
	 * @param game An implementation of {@link Game} that contains the main game logic
	 */
	public Avogine(Window window, Game game) {
		gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
		this.window = window;
		this.game = game;
		timer = new Timer();
		// TODO Add an additional constructor for custom Input implementations as well, maybe even Timer? But idc right now.
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
		window.init(input);
		game.init(window);
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

		while (!window.shouldClose()) {
			// XXX Minor hack to limit frame updates when the window is "held" and instead tie pauses to at least the target UPS
			elapsedTime = Math.min(interval, timer.getElapsedTime());
			accumulator += elapsedTime;
			frameTime += elapsedTime;

			input();

			while (accumulator >= interval) {
				update(interval);
				accumulator -= interval;
			}
			
			render();

			sync();
		}
	}
	
	private void input() {
		input.update();
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
		double endTime = timer.getLastLoopTime() + loopSlot + sleepRemainder;
		
		// Time values are seconds based, so we're subtracting 0.001 to make sure we won't try sleeping for fractions of milliseconds
		while (timer.getTime() < endTime - 0.001) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				logger.warn("Sync operation was interrupted?", e);
				Thread.currentThread().interrupt();
			}
		}
		// If there is any time left over, save it for the next sync
		// This ensures high precision when picking frame rate targets
		sleepRemainder = (float) Math.max(0, endTime - timer.getTime());
	}
	
	private void cleanup() {
		game.cleanup();
		
		Callbacks.glfwFreeCallbacks(window.getId());
		GLFW.glfwDestroyWindow(window.getId());
		
		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
	}

}
