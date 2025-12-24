package com.avogine.concurrent;

import java.time.Duration;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import com.avogine.concurrent.Routine.Avoroutine;
import com.avogine.concurrent.event.Suspendable;
import com.avogine.logging.AvoLog;

/**
 * Demo class of {@link Routine} processing sub routines concurrently with the main game loop.
 */
public class SuspendDemo {

	private static final AtomicBoolean PAUSED = new AtomicBoolean(false);
	
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
				
				if (!PAUSED.get()) {
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
//		AvoLog.log().debug("Avoroutine {} duration: {}ms", AVOROUTINE_NAME.get(), Duration.ofNanos(time).toMillis());
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
	
	private static void launchSubRoutineDemo2(long time, Routine.Avoroutine scope, long routineUpdateInterval) {
		if (Duration.ofNanos(time).toSeconds() > secondsCount) {
			secondsCount++;

			if (secondsCount % 2 == 0) {
				scope.launch("A" + secondsCount, _ -> {
					AvoLog.log().info("Launching avoroutine. {}", scope.getName());
					AtomicInteger scopeCount = new AtomicInteger();
					Predicate<AtomicInteger> countCase = counter -> counter.get() < 50;

					loopSuspending(countCase::test, scopeCount, routineUpdateInterval, _ -> scopeCount.incrementAndGet());

					AvoLog.log().info("Scope {} incremented to: {}", scope.getName(), scopeCount.get());
				});
			}
		}
	}
	
	private static final ScopedValue<Long> FRAME_TIME_INTERVAL = ScopedValue.newInstance();
	
//	private static final ScopedValue<Long> ROUTINE_UPDATE_INTERVAL = ScopedValue.newInstance();
	
	private static void startGameLoopInScope(Consumer<Avoroutine> block) {
		ScopedValue.where(FRAME_TIME_INTERVAL, 1_000_000_000L / 144).run(() -> Routine.runBlocking("GAME_LOOP", block));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long updateRoutineInterval = 1_000_000_000L / 60;
		
		startGameLoopInScope(scope -> {
			boolean running = true;
			
			long realTime = 0L;
			int realTimeUpdateCounter = 0;
			
			long time = 0L;
			long currentTime = System.nanoTime();
			long accumulator = 0L;
			
			while (running) {
				long startTime = System.nanoTime();
				long frameTime = startTime - currentTime;
				currentTime = startTime;
				
				if (!PAUSED.get()) {
					accumulator += frameTime;
					
					while (accumulator >= updateRoutineInterval) {
						launchSubRoutineDemo2(time, scope, updateRoutineInterval);
						time += updateRoutineInterval;
						accumulator -= updateRoutineInterval;
					}
				}
				
				realTime += frameTime;
				// Simulate render frame
				if (Duration.ofNanos(realTime).toSeconds() > realTimeUpdateCounter) {
					realTimeUpdateCounter++;
					AvoLog.log().info("1 second of rendering! Total: {}", realTimeUpdateCounter);
					if (realTimeUpdateCounter == 5) {
						PAUSED.set(true);
						AvoLog.log().info("Pausing update loop. Update timer: {}", secondsCount);
					} else if (realTimeUpdateCounter == 13) {
						PAUSED.set(false);
						AvoLog.log().info("Unpausing update loop. Update timer: {}", secondsCount);
					}
				}
				
//				long totalFrameTime = System.nanoTime() - startTime;
				scope.sleep(Duration.ofNanos(System.nanoTime() - startTime));
//				try {
//					Thread.sleep(Duration.ofNanos(FRAME_TIME_INTERVAL.get() - totalFrameTime));
//				} catch (InterruptedException _) {
//					Thread.currentThread().interrupt();
//				}
			}
		});
//		Routine.runBlocking(scope -> {
//			final long frameTimeInterval = 1_000_000_000 / 144;
//			final long routineUpdateInterval = 1_000_000_000 / 50;
//			
//			boolean running = true;
//			
//			long realTime = 0L;
//			int realTimeUpdateCounter = 0;
//			
//			long time = 0L;
//			long currentTime = System.nanoTime();
//			long accumulator = 0L;
//			
//			while (running) {
//				long startTime = System.nanoTime();
//				long frameTime = startTime - currentTime;
//				currentTime = startTime;
//				
//				if (!PAUSED.get()) {
//					accumulator += frameTime;
//					
//					while (accumulator >= routineUpdateInterval) {
//						launchSubRoutineDemo2(time, scope, routineUpdateInterval);
//						time += routineUpdateInterval;
//						accumulator -= routineUpdateInterval;
//					}
//				}
//				
//				realTime += frameTime;
//				// Simulate render frame
//				if (Duration.ofNanos(realTime).toSeconds() > realTimeUpdateCounter) {
//					realTimeUpdateCounter++;
//					AvoLog.log().info("1 second of rendering! Total: {}", realTimeUpdateCounter);
//					if (realTimeUpdateCounter == 5) {
//						PAUSED.set(true);
//						AvoLog.log().info("Pausing update loop. Update timer: {}", secondsCount);
//					} else if (realTimeUpdateCounter == 13) {
//						PAUSED.set(false);
//						AvoLog.log().info("Unpausing update loop. Update timer: {}", secondsCount);
//					}
//				}
//				
//				long totalFrameTime = System.nanoTime() - startTime;
//				try {
//					Thread.sleep(Duration.ofNanos(frameTimeInterval - totalFrameTime));
//				} catch (InterruptedException _) {
//					Thread.currentThread().interrupt();
//				}
//			}
//		});
	}
}
