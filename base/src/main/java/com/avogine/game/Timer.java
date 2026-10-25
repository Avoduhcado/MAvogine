package com.avogine.game;

import org.lwjgl.glfw.GLFW;

/**
 * A wrapper class for managing calls to GLFW's time methods.
 * </p>
 * When using this {@code Timer} to perform Thread sync operations, you can optionally enable
 * "high precision sleeping" by setting a {@code sleepPrecisionSeconds} value during construction.
 * </p>
 * This method of sleeping can be preferable than just relying on {@code Thread.sleep()} for precision
 * since not only does {@code Thread.sleep()} not support sleeping for less than a millisecond, but also
 * because there's about a 0.5-3ms error rate on waking from the sleep. So using a busy wait loop allows
 * us to more quickly recover from smaller sleep durations. However, it does result in higher CPU usage
 * so may not be the right fit for all applications.
 * 
 * @see <a href="http://andy-malakov.blogspot.com/2010/06/alternative-to-threadsleep.html">http://andy-malakov.blogspot.com/2010/06/alternative-to-threadsleep.html</a>
 * 
 */
public class Timer {

	private static final double DEFAULT_SLEEP_PRECISION_SECONDS = 0.002;
	
	private double sleepPrecisionSeconds;
	
	private double previousTime;
	
	/**
	 * Construct a new {@link Timer} with high precision sleeping optionally enabled.
	 * </p>
	 * This {@code Timer} supports "high precision sleeping" during sync operations issued via {@link #syncThreadUntil(double)} which can produce more stable frame rates but requires more CPU usage as
	 * the sleep loop will effectively put the associated {@code Thread} into a busy wait loop.
	 * 
	 * @param sleepPrecisionSeconds When sleeping a thread every "frame" the system will issue {@link Thread#sleep(long, int)} requests for as long as the
	 * remaining frame time is greater than this value. Once it's below this value it will go into a busy wait until the remaining time is up.
	 * </p>
	 * Certain platforms may have higher error rates on their {@code Thread#sleep(long, int)} precision, and this value could be increased to better suit them. Although a higher value will mean more
	 *  busy waiting leading to more CPU cycling.
	 */
	public Timer(double sleepPrecisionSeconds) {
		if (sleepPrecisionSeconds < DEFAULT_SLEEP_PRECISION_SECONDS) {
			throw new IllegalArgumentException("Sleep precision must be greater than " + DEFAULT_SLEEP_PRECISION_SECONDS);
		}
		this.sleepPrecisionSeconds = sleepPrecisionSeconds;
	}
	
	/**
	 * Construct a new {@link Timer} with default high precision sleep threshold.
	 */
	public Timer() {
		this(DEFAULT_SLEEP_PRECISION_SECONDS);
	}
	
	/**
	 * Initialize this timer to begin tracking elapsed time.
	 */
	public void init() {
		previousTime = getTime();
	}
	
	/**
	 * @return the current time in seconds since GLFW was initialized. This value is expected to have nanosecond precision, but is system dependent.
	 */
	public double getTime() {
		return GLFW.glfwGetTime();
	}
	
	/**
	 * @return the time in seconds since the last call to {@link #getElapsedTime()}.
	 */
	public double getElapsedTime() {
		double time = getTime();
		double elapsedTime = time - previousTime;
		previousTime = getTime();
		return elapsedTime;
	}
	
	/**
	 * @return the previousTime set by {@link #getElapsedTime()}.
	 */
	public double getPreviousTime() {
		return previousTime;
	}
	
	/**
	 * Sleep the current {@code Thread} while {@link #getTime()} is less than {@code endTime}.
	 * </p>
	 * This will perform a regular {@link Thread#sleep(long, int)} operation while the remaining sleep time is greater than {@code sleepPrecisionSeconds}. If the remaining time is less than
	 * {@code sleepPrecisionSeconds} then the thread will enter a busy loop via {@link Thread#onSpinWait()} for the remainder of the time.
	 * @param endTime endTime The time in seconds relative to {@link #getTime()} to sleep until. A past time indicates a no-op.
	 */
	public void syncThreadUntil(double endTime) {
		double seconds = endTime - getTime();

		/*
		 * Extract just the millisecond value. (Seconds * 1,000)
		 * Negative values are ignored and clamped to 0.
		 */
		double totalMillisDec = seconds * 1_000.0;
		long totalMillis = Math.max(0, (long) totalMillisDec);

		/* 
		 * Extract just the nanosecond value. (Milliseconds % 1) * 1,000,000
		 * (Milliseconds % 1) to retrieve just the nanoseconds part of the total milliseconds.
		 * * 1,000,000 to convert the decimal nanoseconds to a whole number, clamped from 0 -> 999,999
		 */
		double totalNanosDec = totalMillisDec % 1;
		int totalNanos = Math.clamp((long) (totalNanosDec * 1_000_000L), 0, 999_999);

		double remaining = seconds;
		while (remaining > sleepPrecisionSeconds) {
			try {
				// Perform normal sleep for the remaining milliseconds - 1ms (to account for accuracy issues).
				Thread.sleep(Math.max(0, totalMillis - 1), totalNanos);
			} catch (InterruptedException _) {
				Thread.currentThread().interrupt();
				return;
			}
			remaining = endTime - getTime();
		}
		
		while (getTime() < endTime) {
			Thread.onSpinWait();
		}
	}

}
