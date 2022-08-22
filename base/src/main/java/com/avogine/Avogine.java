package com.avogine;

import java.util.concurrent.*;

import com.avogine.game.*;
import com.avogine.io.*;
import com.avogine.logging.*;

/**
 * This is the primary entry point into running a game.
 * <p>
 * To kick off the game loop, create a new {@link Avogine} and supply it with a {@link Window} to render
 * to and a {@link Game} to run, then call {@link #start()}.
 *
 */
public class Avogine implements Runnable {

	/**
	 * Target updates per second. Controls how often game logic is run in a single second.
	 */
	public static final int TARGET_UPS = 60;
	
	private static final long SLEEP_PRECISION = TimeUnit.MILLISECONDS.toNanos(2);

	private final Thread gameLoopThread;
	
	private final Window window;
	private final Game game;
	
	private final Input input;
	private final Timer timer;
	
	/**
	 * @param window The primary game {@link Window} to display to
	 * @param game An implementation of {@link Game} that contains the main game logic
	 */
	public Avogine(Window window, Game game) {
		gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
		this.window = window;
		this.game = game;
		// TODO Add an additional constructor for custom Input implementations as well, maybe even Timer? But idc right now.
		input = new Input();
		timer = new Timer();
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
			AvoLog.log().info("Setting java.awt.headless to true");
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
			AvoLog.log().error("An error occurred.", e);
		} finally {
			cleanup();
		}
	}
	
	private void init() {
		window.init(input);
		game.init(window);
		timer.init();
	}
	
	private void loop() {
		double updateInterval = 1.0 / TARGET_UPS;
		
		double previousFrameTime = timer.getTime();
		double accumulator = 0.0;
				
		while (!window.shouldClose()) {
			double frameStartTime = timer.getTime();
			double elapsedTime = (frameStartTime - previousFrameTime);
			window.setFps((int) (1 / elapsedTime));
			accumulator += elapsedTime;
			
			input();
			
			while (accumulator > updateInterval) {
				update((float) updateInterval);
				accumulator -= updateInterval;
			}
			
			render();

			previousFrameTime = frameStartTime;
			// Get the time it took to actually process the entire frame (this should be counted as a segment of our overall time dictated by renderInterval)
			double frameProcessTimeNanos = timer.getTime() - frameStartTime;
			long sleepTime = (long) (Math.max(window.getTargetFps() - frameProcessTimeNanos, 0) * 1_000_000_000); // Convert to nanoseconds
			
			try {
				sleepNanos(sleepTime);
			} catch (InterruptedException e) {
				AvoLog.log().warn("Sync operation was interrupted?", e);
				Thread.currentThread().interrupt();
			}
		}
	}
	
	private void input() {
		input.update();
	}

	private void update(float interval) {
		// TODO Process EventQueue
		game.update(interval);
	}
	
	private void render() {
		game.render();
		window.update();
	}
	
	/**
	 * Perform regular {@link Thread#sleep()} while the remaining sleep duration is greater than {@code Avogine.SLEEP_PRECISION}.
	 * If the remaining time is less than {@code Avogine.SLEEP_PRECISION} then perform a {@link Thread#onSpinWait()} for the remainder of the time.
	 * </p>
	 * This method is preferable than just relying on {@code Thread.sleep()} since not only does {@code Thread.sleep()} not support sleeping for less
	 * than a millisecond on Windows, but also because there's about a 1-2ms error rate on waking from the sleep so using a busy wait loop allows us
	 * to more quickly recover from smaller sleep durations.
	 * 
	 * @see <a href="http://andy-malakov.blogspot.com/2010/06/alternative-to-threadsleep.html">http://andy-malakov.blogspot.com/2010/06/alternative-to-threadsleep.html</a>
	 * 
	 * @param nanoDuration The amount of time to sleep for in nanoseconds.
	 * @throws InterruptedException
	 */
	private static void sleepNanos(long nanoDuration) throws InterruptedException {
		final long end = System.nanoTime() + nanoDuration;

		long timeLeft = nanoDuration;
		do {
			if (timeLeft > SLEEP_PRECISION)
				Thread.sleep(1);
			else {
				// Potentially opt instead for spin waiting while greater than some other spin_yield_precision value
				Thread.onSpinWait();
			}
			
			timeLeft = end - System.nanoTime();
			
			if (Thread.interrupted()) {
				throw new InterruptedException();
			}
		} while (timeLeft > 0);
	}
	
	private void cleanup() {
		game.cleanup();
		
		window.cleanup();
		
		// When using coroutines, the default context utilizes the commonPool, which needs to be manually stopped before the system exits.
		ForkJoinPool.commonPool().awaitQuiescence(1000, TimeUnit.MILLISECONDS);
		System.exit(0);
	}

}
