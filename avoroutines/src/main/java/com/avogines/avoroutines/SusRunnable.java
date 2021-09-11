package com.avogines.avoroutines;

/**
 *
 */
@FunctionalInterface
public interface SusRunnable {

	public void run() throws SuspendExecution;
	
}
