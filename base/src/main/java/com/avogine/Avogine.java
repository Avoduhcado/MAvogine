package com.avogine;

import java.util.*;
import java.util.concurrent.*;

import com.avogine.game.*;
import com.avogine.game.Timer;
import com.avogine.io.Window;
import com.avogine.io.config.WindowPreferences;
import com.avogine.logging.AvoLog;
import com.avogine.util.FrameProfiler;

/**
 * This is the primary entry point into running a game.
 * <p>
 * To kick off the game loop, create a new {@link Avogine} and supply it with a {@link Window} to render
 * to and a {@link HotGame} to run, then call {@link #start()}.
 *
 */
public class Avogine implements Runnable {

	/**
	 * When sleeping the game loop thread per frame, the system will issue {@code Thread.sleep(1)} requests for as long as the
	 * remaining frame time is greater than this value. Once it's below this value it will go into a busy wait until 
	 * the frame time is up.
	 * </p>
	 * Certain platforms may have higher error rates on their {@code Thread.sleep()} precision, and this value could be
	 * increased to better suit them. Although a higher value will mean more busy waiting leading to more CPU cycling.
	 */
	private static final long SLEEP_PRECISION = TimeUnit.MILLISECONDS.toNanos(2);

	private final Thread gameLoopThread;
	private final FrameProfiler profiler;
	
	/**
	 * The main window should be initialized on the main thread and maintain initializing GLFW.
	 */
	private final Window mainWindow;
	private final Game game;
	private static final List<Window> WINDOW_CACHE = new ArrayList<>();
	
	private final Timer timer;
	
	private double updateInterval;
	private double updateAccumulator;
	
	/**
	 * @param title The title of the main {@link Window}.
	 * @param preferences The {@link WindowPreferences} to apply to the main Window.
	 * @param game An implementation of {@link Game} that contains the main game logic
	 */
	public Avogine(String title, WindowPreferences preferences, Game game) {
		gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
		profiler = FrameProfiler.NO_OP;
		
		mainWindow = new Window(title, preferences, () -> {
			resize();
			return null;
		});
		this.game = game;
		
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
		mainWindow.init(game.getGLFWConfig(), game.getInputConfig());
		game.init(mainWindow);
		timer.init();
	}
	
	private void loop() {
		updateInterval = 1.0 / game.getTargetUps();
		
		double previousFrameTime = timer.getTime();
		double loopTime = 0;
		
		while (!mainWindow.shouldClose()) {
			profiler.startFrame();
			double frameStartTime = timer.getTime();
			double elapsedTime = (frameStartTime - previousFrameTime);
			previousFrameTime = frameStartTime;
			mainWindow.setFps((int) (1 / elapsedTime));
			updateAccumulator += elapsedTime;
			
			loopTime += elapsedTime;
			if (loopTime >= 1) {
				profiler.printAverages();
				loopTime = 0;
			}

			input();

			update();
			
			render();
			
			profiler.endFrame();
			// Get the time it took to actually process the entire frame (this should be counted as a segment of our overall time dictated by renderInterval)
			double frameProcessTimeNanos = timer.getTime() - frameStartTime;
			long sleepTime = (long) (Math.max(mainWindow.getTargetFps() - frameProcessTimeNanos, 0) * 1_000_000_000); // Convert to nanoseconds
			
			try {
				sleepNanos(sleepTime);
			} catch (InterruptedException e) {
				AvoLog.log().warn("Sync operation was interrupted?", e);
				Thread.currentThread().interrupt();
			}
			profiler.endBudget();
		}
	}
	
	private void input() {
		profiler.inputStart();

		game.input(mainWindow);
		
		profiler.inputEnd();
	}

	private void update() {
		profiler.updateStart();
		
		while (updateAccumulator > updateInterval) {
			// TODO Process EventQueue
			game.update((float) updateInterval);
			updateAccumulator -= updateInterval;
		}
		
		profiler.updateEnd();
	}
	
	private void render() {
		profiler.renderStart();
		
		game.render(mainWindow);
		mainWindow.swapBuffers();
		
		profiler.renderEnd();
	}
	
	private void resize() {
		int width = mainWindow.getWidth();
		int height = mainWindow.getHeight();
		game.resize(width, height);
	}
	
	/**
	 * Perform regular {@link Thread#sleep()} while the remaining sleep duration is greater than {@code Avogine.SLEEP_PRECISION}.
	 * If the remaining time is less than {@code Avogine.SLEEP_PRECISION} then perform {@link Thread#onSpinWait()} for the remainder of the time.
	 * </p>
	 * This method is preferable than just relying on {@code Thread.sleep()} since not only does {@code Thread.sleep()} not support sleeping for less
	 * than a millisecond, but also because there's about a 0.5-3ms error rate on waking from the sleep so using a busy wait loop allows us to more 
	 * quickly recover from smaller sleep durations.
	 * 
	 * @see <a href="http://andy-malakov.blogspot.com/2010/06/alternative-to-threadsleep.html">http://andy-malakov.blogspot.com/2010/06/alternative-to-threadsleep.html</a>
	 * 
	 * @param nanoDuration The amount of time to sleep for in nanoseconds.
	 * @throws InterruptedException
	 */
	private static void sleepNanos(long nanoDuration) throws InterruptedException {
		final long end = System.nanoTime() + nanoDuration;

		long timeLeft = nanoDuration;
		while (timeLeft > 0) {
			if (timeLeft > SLEEP_PRECISION) {
				Thread.sleep(1);
			} else {
				Thread.onSpinWait();
			}
			
			timeLeft = end - System.nanoTime();
			
			if (Thread.interrupted()) {
				throw new InterruptedException();
			}
		}
	}
	
	public static List<Window> getWindowCache() {
		return WINDOW_CACHE;
	}
	
	private void cleanup() {
		game.cleanup();
		
		mainWindow.cleanup();
		
		// When using coroutines, the default context utilizes the commonPool, which needs to be manually stopped before the system exits.
		ForkJoinPool.commonPool().awaitQuiescence(1000, TimeUnit.MILLISECONDS);
		System.exit(0);
	}

}
