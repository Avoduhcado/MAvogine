package com.avogine.game;

import java.lang.invoke.MethodHandles;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avogine.experimental.annotation.InDev;

/**
 * @author Dominus
 *
 */
@InDev
public class AvoPlay implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

	private final Thread gameLoopThread;
	private final Theater theater;
	
	/**
	 * @param theater 
	 * 
	 */
	public AvoPlay(Theater theater) {
		gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
		this.theater = theater;
	}
	
	/**
	 * 
	 */
	public void start() {
		// Macs handle Threads differently
		// Also GLFW and AWT don't play nice under Mac so we need to go headless
		String osName = System.getProperty("os.name");
		if (osName.contains("Mac")) {
			System.setProperty("java.awt.headless", "true");
			logger.info("Setting java.awt.headless to true");
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
			logger.error("An error occurred.", e);
		} finally {
			cleanup();
		}
	}
	
	private void init() {
		theater.init();
	}
	
	private void loop() {
		while (!theater.shouldClose()) {
			// Input should also be run here
			theater.update();
		}
	}
	
	private void cleanup() {
		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
	}
	
	/**
	 * @return the theater
	 */
	public Theater getTheater() {
		return theater;
	}
	
}
