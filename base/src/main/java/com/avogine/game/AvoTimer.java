package com.avogine.game;

import java.util.concurrent.*;

import org.lwjgl.glfw.*;

/**
 *
 */
public class AvoTimer {

	private int targetFPS = 60;
	
	private int fps = 0;
	private long frameTime = 0;
	
	private long previous = 0;
	private long elapsedTime = 0;
	
	public void syncSleep(long nanoTime) {
		try {
			fps++;
			
//			TimeUnit.NANOSECONDS.sleep(nanoTime);
//			this.wait(nanoTime / 1000000, (int) (nanoTime % 1000000));
			Thread.sleep(15);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
	public void run() {
		
		GLFW.glfwInit();
		
		previous = (long) (GLFW.glfwGetTime() * 1000L);
		
		long oneSecondInMicroseconds = TimeUnit.MICROSECONDS.convert(1, TimeUnit.SECONDS);
		System.out.println(oneSecondInMicroseconds);
		long timeToSleepPerFrameInMicroseconds = oneSecondInMicroseconds / targetFPS;
		System.out.println(timeToSleepPerFrameInMicroseconds);
		long oneSecondInNanoseconds = TimeUnit.NANOSECONDS.convert(1, TimeUnit.SECONDS);
		System.out.println(oneSecondInNanoseconds);
		long timeToSleepPerFrameInNanoseconds = oneSecondInNanoseconds / targetFPS;
		System.out.println("Nanotime " + timeToSleepPerFrameInNanoseconds);
		
		int counter = 0;
		
		boolean running = true;
		while(running) {
			long currentTime = (long) (GLFW.glfwGetTime() * 1000L);
			long elapsedTime = currentTime - previous;
			System.out.println("e " + elapsedTime);
//			System.out.println(elapsedTime + " Expected: " + timeToSleepPerFrameInNanoseconds + " Difference: " + (elapsedTime - timeToSleepPerFrameInNanoseconds));
			previous = currentTime;
			frameTime += elapsedTime;
			
//			elapsedTime = System.currentTimeMillis() - lastLoopTime;
//			lastLoopTime = System.currentTimeMillis();
//			long millis = TimeUnit.NANOSECONDS.convert(elapsedTime, TimeUnit.MILLISECONDS);
//			System.out.println(millis);
//			
//			System.out.println("Dicks");
			if (frameTime >= 1000) {
				System.out.println("FPS " + fps);
				fps = 0;
				frameTime = 0;
				counter++;
				if (counter > 5) {
					running = false;
				}
			}
			syncSleep(timeToSleepPerFrameInNanoseconds);
		}
	}
	
	public static void main(String[] args) {
		var sync = new AvoTimer();

		sync.run();
	}
	
}
