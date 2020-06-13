package com.avogine;

import java.lang.invoke.MethodHandles;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avogine.game.Stage;
import com.avogine.game.Timer;
import com.avogine.game.Window;

/**
 * @author Dominus
 *
 */
public class Play implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

	/**
	 * Target frames per second. Controls how often the screen is rendered in a single second.
	 */
	public static final int TARGET_FPS = 60;

	/**
	 * Target updates per second. Controls how often game logic is run in a single second.
	 */
	public static final int TARGET_UPS = 60;
	
	// TODO Multiple Theaters in a list
	// TODO Better Theater/Window and Stage hierarchy
	private final Window window;
	private final Stage stage;
	private final Thread gameLoopThread;
	private final Timer timer;
	
	private float frameTime;
	private int fps;
	
	/**
	 * @param window
	 * @param stage
	 */
	public Play(Window window, Stage stage) {
		gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
		this.window = window;
		this.stage = stage;
		this.timer = new Timer();
	}
	
	/**
	 * 
	 */
	public void start() {
		// Macs handle Threads differently
		// Also GLFW and AWT don't play nice under Mac so we need to go headless
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
		stage.init(window);
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
		stage.input(window);
	}
	
	private void render() {
		fps++;
		if (frameTime >= 1.0f) {
			window.setFps(fps);
			frameTime = 0;
			fps = 0;
		}
		stage.render(window);
		window.render();
	}
	
	private void update(float interval) {
		// TODO Process EventQueue
		stage.update(interval, window);
		window.update();
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
		stage.cleanup();
		
		Callbacks.glfwFreeCallbacks(window.getId());
		GLFW.glfwDestroyWindow(window.getId());
		
		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
	}

}
