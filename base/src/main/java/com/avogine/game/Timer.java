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
	 * A {@code sleepPrecisionSeconds} value greater than 0.0 will enable "high precision sleeping" during sync operations
	 * issued via {@link #syncThreadUntil(double)} which can produce more stable frame rates but requires more CPU usage
	 * as the sleep loop will effectively put the associated Thread into a busy wait loop.
	 * 
	 * @param sleepPrecisionSeconds When sleeping a thread every "frame" the system will issue {@code Thread.sleep(1)} requests for as long as the
	 * remaining frame time is greater than this value. Once it's below this value it will go into a busy wait until 
	 * the frame time is up.
	 * </p>
	 * Certain platforms may have higher error rates on their {@code Thread.sleep()} precision, and this value could be
	 * increased to better suit them. Although a higher value will mean more busy waiting leading to more CPU cycling.
	 */
	public Timer(double sleepPrecisionSeconds) {
		this.sleepPrecisionSeconds = sleepPrecisionSeconds;
	}
	
	/**
	 * Construct a new {@link Timer} with high precision sleeping optionally enabled to a default value.
	 * @param highPrecisionSleep true to enable "high precision sleeping" with a default configured precision.
	 */
	public Timer(boolean highPrecisionSleep) {
		this(highPrecisionSleep ? DEFAULT_SLEEP_PRECISION_SECONDS : 0);
	}
	
	/**
	 * Construct a new {@link Timer} with high precision sleeping disabled.
	 */
	public Timer() {
		this(0);
	}
	
	/**
	 * Initialize this timer to begin tracking elapsed time.
	 */
	public void init() {
		previousTime = getTime();
	}
	
	/**
	 * @return the current time in seconds since GLFW was initialized.
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
	 * If the remaining time is less than {@code sleepPrecisionSeconds} then perform {@link Thread#onSpinWait()} for the remainder of the time.
	 * 
	 * @param endTime The time, offset from {@link #getTime()} to sleep until. A past time indicates a no-op.
	 * @throws InterruptedException if the current Thread was interrupted while sleeping.
	 */
	public void syncThreadUntil(double endTime) throws InterruptedException {
		double sleepTime;
		while ((sleepTime = endTime - getTime()) > 0) {
			if (sleepTime <= sleepPrecisionSeconds) {
				Thread.onSpinWait();
			} else {
				Thread.sleep(1);
			}
		}
	}

}
