package com.avogine.avoroutines;

/**
 *
 */
@FunctionalInterface
public interface SusRunnable {

	public void run() throws SuspendExecution;
	
}
