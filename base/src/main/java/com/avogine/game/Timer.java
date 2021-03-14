package com.avogine.game;

/**
 *
 */
public class Timer {

	private double lastLoopTime;

	/**
	 * 
	 */
	public void init() {
		lastLoopTime = getTime();
	}

	/**
	 * @return
	 */
	public double getTime() {
		return System.nanoTime() / 1000_000_000.0;
	}

	/**
	 * @return
	 */
	public float getElapsedTime() {
		double time = getTime();
		float elapsedTime = (float) (time - lastLoopTime);
		lastLoopTime = time;
		return elapsedTime;
	}

	/**
	 * @return
	 */
	public double getLastLoopTime() {
		return lastLoopTime;
	}
	
}
