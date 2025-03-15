package com.avogine.concurrent.util;

import java.time.Duration;
import java.util.concurrent.*;

import com.avogine.logging.AvoLog;

/**
 *
 */
public class SleepUtils {

	/**
	 * When sleeping the game loop thread per frame, the system will issue {@code Thread.sleep(1)} requests for as long as the
	 * remaining frame time is greater than this value. Once it's below this value it will go into a busy wait until 
	 * the frame time is up.
	 * </p>
	 * Certain platforms may have higher error rates on their {@code Thread.sleep()} precision, and this value could be
	 * increased to better suit them. Although a higher value will mean more busy waiting leading to more CPU cycling.
	 */
	private static final long SLEEP_PRECISION_MS = 2;
	
	private static ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
	
	/**
	 * Perform regular {@link Thread#sleep(long)} while the remaining sleep duration is greater than {@code Avogine.SLEEP_PRECISION}.
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
	public static void sleepHighPrecision(long nanoDuration) throws InterruptedException {
		final long end = System.nanoTime() + nanoDuration;

		long current = System.nanoTime();
		while (current < end) {
			var remaining = Duration.ofNanos(end).minusNanos(current).toMillis();
			if (remaining > SLEEP_PRECISION_MS) {
				Thread.sleep(remaining - SLEEP_PRECISION_MS);
			} else {
//				Thread.onSpinWait();
			}
			current = System.nanoTime();
		}
	}
	
	public static void scheduleWork(int fps) throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(fps);
		ses.scheduleAtFixedRate(latch::countDown, 0, 1_000_000_000 / fps, TimeUnit.NANOSECONDS);
		latch.await();
	}
	
	public static void spinWaitWork(long fps) throws InterruptedException {
		int count = 0;
		do {
			sleepHighPrecision(1_000_000_000 / fps);
			count++;
		} while (count < fps);
	}
	
	public static void main(String[] args) throws InterruptedException {
//		for (int i = 0; i < 1000; i++) {
//			long startNano = System.nanoTime();
//			scheduleWork(500);
//			AvoLog.log().info("Scheduled duration: {}ms", Duration.ofNanos(System.nanoTime() - startNano).toMillis());
//		}
		
		for (int i = 0; i < 1000; i++) {
			long startNano = System.nanoTime();
			spinWaitWork(144);
			AvoLog.log().info("SpinWait duration: {}ms", Duration.ofNanos(System.nanoTime() - startNano).toMillis());
		}
	}
	
}
