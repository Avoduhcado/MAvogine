package com.avogine.core.system;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;

import com.avogine.core.scene.Stage;

public class Play implements Runnable {

	/**
	 * Target frames per second. Controls how often the screen is rendered in a single second.
	 */
	public static final int TARGET_FPS = 60;

	/**
	 * Target updates per second. Controls how often game logic is run in a single second.
	 */
	public static final int TARGET_UPS = 30;
	
	private final Theater theater;
	private final Stage stage;
	private final Thread gameLoopThread;
	private final Timer timer;
	
	private float frameTime;
	private int fps;
	
	public Play(Theater theater, Stage stage) {
		gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
		this.theater = theater;
		this.stage = stage;
		this.timer = new Timer();
	}
	
	public void start() {
		// Macs handle Threads differently
		// Also GLFW and AWT don't play nice under Mac so we need to go headless
		String osName = System.getProperty("os.name");
		if (osName.contains("Mac")) {
			System.setProperty("java.awt.headless", "true");
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
			System.err.println("An error occurred");
			e.printStackTrace();
		} finally {
			cleanup();
		}
	}
	
	private void init() {
		theater.init();
		stage.init(theater);
		timer.init();
		
		// XXX This sort of works, but only when the window is resized, not while it's being moved/held
//		GLFW.glfwSetWindowRefreshCallback(theater.getId(), (window) -> {
//			System.out.println("Refreshing");
//			
//			update(0.045f);
//			
//			fps++;
//			if (frameTime >= 1.0f) {
//				theater.setFps(fps);
//				frameTime = 0;
//				fps = 0;
//			}
//			stage.render();
//			
//			GLFW.glfwSwapBuffers(window);
//		});
		
	}
	
	/**
	 * <p>Call once to kick off game loop.
	 * 
	 * <p>Runs as long as {@link #theater} is not set to close as specified by {@link GLFW#glfwWindowShouldClose(long)}.
	 * In order, once per frame the {@link #frameTime} is calculated, input is processed, update code is run, the screen is rendered, and then also synced.
	 */
	private void loop() {
		float elapsedTime;
		float accumulator = 0f;
		float interval = 1f / TARGET_UPS;

		boolean running = true;
		while (running && !GLFW.glfwWindowShouldClose(theater.getId())) {
			// XXX Minor hack to limit frame updates when the window is "held" and instead tie pauses to at least the target UPS
			elapsedTime = Math.min(interval, timer.getElapsedTime());
			accumulator += elapsedTime;
			frameTime += elapsedTime;

			input(theater);

			while (accumulator >= interval) {
				update(interval);
				accumulator -= interval;
			}
			
			render();

			sync();
		}
	}
	
	private void input(Theater theater) {
		// TODO Change this to call Input.update() over a collection of Theaters
		stage.input(theater);
	}
	
	private void render() {
		fps++;
		if (frameTime >= 1.0f) {
			theater.setFps(fps);
			frameTime = 0;
			fps = 0;
		}
		stage.render();
		theater.render();
	}
	
	private void update(float interval) {
		// TODO Process EventQueue
		stage.update(interval, theater);
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
		
		Callbacks.glfwFreeCallbacks(theater.getId());
		GLFW.glfwDestroyWindow(theater.getId());
		
		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
	}

}
