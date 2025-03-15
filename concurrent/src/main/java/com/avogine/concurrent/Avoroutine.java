package com.avogine.concurrent;

import java.time.*;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import com.avogine.concurrent.event.Suspendable;
import com.avogine.concurrent.scope.SupervisorTaskScope;
import com.avogine.logging.AvoLog;

/**
 *
 */
public class Avoroutine {
	
	private static final AtomicBoolean paused = new AtomicBoolean(false);
	
	private Avoroutine() {
	}

	/**
	 * @param block
	 * @throws InterruptedException
	 */
	public static void runBlocking(Consumer<SupervisorTaskScope> block) throws InterruptedException {
		long startTime = System.nanoTime();
		
		try (var scope = new SupervisorTaskScope("Avoroutines")) {
			block.accept(scope);
			
			try {
				scope.joinUntil(Instant.now().plusMillis(100));
			} catch (TimeoutException e) {
				AvoLog.log().warn("Timeout occured.", e);
			}
		} finally {
			AvoLog.log().debug("Block duration: {}ms", Duration.ofNanos(System.nanoTime() - startTime).toMillis());
		}
	}
	
	/**
	 * @param <T>
	 * @param condition
	 * @param testCase
	 * @param frameTimeInterval
	 * @param syncDelay
	 * @param action
	 */
	public static <T> void loopSuspending(Predicate<T> condition, T testCase, long frameTimeInterval, long syncDelay, DoubleConsumer action) {
		long time = 0;
		long currentTime = System.nanoTime();
		long accumulator = 0;

		try {
			while (condition.test(testCase)) {
				long startTime = System.nanoTime();
				long frameTime = startTime - currentTime;
				currentTime = startTime;
				
				if (!paused.get()) {
					accumulator += frameTime;

					while (accumulator >= frameTimeInterval) {
						action.accept(frameTimeInterval);
						time += frameTimeInterval;
						accumulator -= frameTimeInterval;
					}
				}
				
				long totalFrameTime = System.nanoTime() - startTime;
				Thread.sleep(Duration.ofMillis(syncDelay - Duration.ofNanos(totalFrameTime).toMillis()));
			}
		} catch (InterruptedException _) {
			Thread.currentThread().interrupt();
		}
		AvoLog.log().debug("Avoroutine duration: {}ms", Duration.ofNanos(time).toMillis());
	}
	
	/**
	 * @param <T>
	 * @param condition
	 * @param testCase
	 * @param frameTimeInterval
	 * @param action
	 */
	public static <T> void loopSuspending(Predicate<T> condition, T testCase, long frameTimeInterval, DoubleConsumer action) {
		// TODO If no syncDelay is specified, refer to some globally set background job sync time?
		loopSuspending(condition, testCase, frameTimeInterval, 1000 / 50, action);
	}
	
	/**
	 * @param <T>
	 * @param block
	 */
	public static <T> void loopSuspending(Suspendable<T> block) {
		loopSuspending(block.condition(), block.testCase(), block.targetFrameTime(), block.action());
	}
	
	private static long secondsCount;
	
	private static void launchSubRoutineDemo(long time, SupervisorTaskScope scope, long routineUpdateInterval) {
		if (Duration.ofNanos(time).toSeconds() > secondsCount) {
			secondsCount++;

			if (secondsCount % 2 == 0) {
				final String routineName = "A" + secondsCount;
				AvoLog.log().info("Launching avoroutine. {}", routineName);
				scope.launch(() -> {
					AtomicInteger scopeCount = new AtomicInteger();
					Predicate<AtomicInteger> countCase = counter -> counter.get() < 50;

					loopSuspending(countCase::test, scopeCount, routineUpdateInterval, _ -> scopeCount.incrementAndGet());

					AvoLog.log().info("Scope {} incremented to: {}", routineName, scopeCount.get());
				});
			}
		}
	}
	
	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		runBlocking(scope -> {
			final long frameTimeInterval = 1_000_000_000 / 144;
			final long routineUpdateInterval = 1_000_000_000 / 50;
			
			boolean running = true;
			
			long realTime = 0L;
			int realTimeUpdateCounter = 0;
			
			long time = 0L;
			long currentTime = System.nanoTime();
			long accumulator = 0L;
			
			// TODO Add PrimaryTaskScope and use forkPrimary?
			while (running) {
				long startTime = System.nanoTime();
				long frameTime = startTime - currentTime;
				currentTime = startTime;
				
				if (!paused.get()) {
					accumulator += frameTime;
					
					while (accumulator >= routineUpdateInterval) {
						launchSubRoutineDemo(time, scope, routineUpdateInterval);
						time += routineUpdateInterval;
						accumulator -= routineUpdateInterval;
					}
				}
				
				realTime += frameTime;
				// Simulate render frame
				if (Duration.ofNanos(realTime).toSeconds() > realTimeUpdateCounter) {
					realTimeUpdateCounter++;
					AvoLog.log().info("1 second of rendering! Total: {}", realTimeUpdateCounter);
					if (realTimeUpdateCounter == 5) {
						paused.set(true);
						AvoLog.log().info("Pausing update loop. Update timer: {}", secondsCount);
					} else if (realTimeUpdateCounter == 13) {
						paused.set(false);
						AvoLog.log().info("Unpausing update loop. Update timer: {}", secondsCount);
					}
				}
				
				long totalFrameTime = System.nanoTime() - startTime;
				try {
					Thread.sleep(Duration.ofNanos(frameTimeInterval - totalFrameTime));
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		});
	}
}
